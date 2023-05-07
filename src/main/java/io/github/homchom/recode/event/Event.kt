package io.github.homchom.recode.event

import com.google.common.cache.CacheBuilder
import io.github.homchom.recode.lifecycle.CoroutineModule
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.util.collections.concurrentSet
import io.github.homchom.recode.util.coroutines.RendezvousFlow
import io.github.homchom.recode.util.coroutines.timer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.event.Event
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

typealias EventInvoker<T> = (context: T) -> Unit

/**
 * Creates a [CustomEvent], providing results by transforming context with [resultCapture].
 */
fun <T, R : Any> createEvent(resultCapture: (T) -> R): CustomEvent<T, R> = FlowEvent(resultCapture)

/**
 * Creates a [CustomEvent] with a Unit result.
 */
fun <T> createEvent() = createEvent<T, Unit> {}

/**
 * Creates a buffered [CustomEvent] that runs asynchronously and caches the result on [interval].
 *
 * @param keySelector A function that transforms [T] into a hashable key type [K] for the buffer.
 * @param cacheDuration How long event contexts should stay in the cache after being written before eviction.
 */
fun <T, R : Any, K : Any> createBufferedEvent(
    resultCapture: (T) -> R,
    interval: Duration,
    keySelector: (T) -> K,
    cacheDuration: Duration = 1.seconds
): CustomEvent<T, R> {
    return BufferedEvent(createEvent(resultCapture), interval, keySelector, cacheDuration)
}

/**
 * Wraps an existing Fabric [Event] into a [Listenable], using [transform] to map recode listeners to its
 * specification.
 */
fun <T, L> wrapFabricEvent(
    event: Event<L>,
    transform: (EventInvoker<T>) -> L
): WrappedEvent<T, L> {
    return EventWrapper(event, transform)
}

/**
 * A custom [Listenable] event that can be [run]. Event contexts are transformed into "results", which are
 * returned by [run].
 */
interface CustomEvent<T, R : Any> : Listenable<T> {
    suspend fun run(context: T): R

    fun runBlocking(context: T) = runBlocking { run(context) }
}

/**
 * A Fabric [Event] wrapped into a [Listenable].
 */
interface WrappedEvent<T, L> : Listenable<T> {
    val invoker: L
}

// TODO: revisit Detector and Requester interfaces (should more be exposed/documented? less?)

/**
 * A [Listenable] that is run algorithmically, based on another Listenable.
 *
 * @param T The type passed as input to the detector.
 * @param R The detector's event context type for detected results.
 *
 * @property timeoutDuration The maximum duration used in detection functions.
 */
interface Detector<T : Any, R : Any> : Listenable<R> {
    val timeoutDuration: Duration

    /**
     * Listens for [basis] invocations from [module] until a match is found.
     *
     * @returns The event result, or null if one could not be found in time.
     */
    suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>? = null): R?

    /**
     * Listens to the next [basis] invocation from [module] and returns a potential match.
     *
     * @returns The event result, or null if there was not a match.
     */
    suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>? = null, attempts: UInt = 1u): R?
}

/**
 * A [Detector] that can execute code to request a result before detecting it.
 */
interface Requester<T : Any, R : Any> : Detector<T, R> {
    /**
     * Makes a request and detects the result.
     *
     * @see detectFrom
     */
    suspend fun requestFrom(module: RModule, input: T): R

    /**
     * Makes a request and detects the result only from the next invocation.
     *
     * @see checkNextFrom
     */
    suspend fun requestNextFrom(module: RModule, input: T, attempts: UInt = 1u): R
}

/**
 * @see Detector
 * @see RModule
 */
interface DetectorModule<T : Any, R : Any> : Detector<T, R>, RModule

/**
 * @see Requester
 * @see RModule
 */
interface RequesterModule<T : Any, R : Any> : Requester<T, R>, RModule

private class FlowEvent<T, R : Any>(private val resultCapture: (T) -> R) : CustomEvent<T, R> {
    private val flow = RendezvousFlow<T>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getNotificationsFrom(module: RModule) = flow

    override suspend fun run(context: T): R {
        flow.emitAndAwait(context)
        return resultCapture(context)
    }
}

private class BufferedEvent<T, R : Any, K : Any>(
    delegate: CustomEvent<T, R>,
    interval: Duration,
    private val keySelector: (T) -> K,
    cacheDuration: Duration = 1.seconds,
    private val module: CoroutineModule = exposedModule()
) : CustomEvent<T, R> {
    private val delegate = DependentEvent(delegate, module)

    // TODO: use Caffeine (official successor) or other alternative?
    private val buffer = CacheBuilder.newBuilder()
        .expireAfterAccess(cacheDuration.toJavaDuration())
        .build<K, R>()

    private val currentSet = concurrentSet<K>()

    init {
        timer(interval)
            .onEach { currentSet.clear() }
            .launchIn(module)
    }

    override fun getNotificationsFrom(module: RModule) = delegate.getNotificationsFrom(module)

    override suspend fun run(context: T): R {
        val key = keySelector(context)
        val bufferResult = buffer.getIfPresent(key)
        val result = if (bufferResult == null) {
            currentSet.add(key)
            delegate.run(context).also { buffer.put(key, it) }
        } else {
            if (currentSet.add(key)) {
                module.launch {
                    buffer.put(key, delegate.run(context))
                }
            }
            bufferResult
        }
        return result
    }
}

private class EventWrapper<T, L>(
    private val fabricEvent: Event<L>,
    transform: ((T) -> Unit) -> L
) : WrappedEvent<T, L> {
    private val async = createEvent<T>()

    override val invoker: L get() = fabricEvent.invoker()

    init {
        fabricEvent.register(transform(async::runBlocking))
    }

    override fun getNotificationsFrom(module: RModule) = async.getNotificationsFrom(module)
}