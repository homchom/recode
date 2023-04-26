package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import net.fabricmc.fabric.api.event.Event
import kotlin.time.Duration

typealias EventListener<T> = (context: T) -> Unit

typealias SimpleValidatedEvent<T> = CustomEvent<SimpleValidated<T>, Boolean>

/**
 * Creates a [CustomEvent], providing results by transforming context with [resultCapture].
 */
fun <T, R : Any> createEvent(resultCapture: T.() -> R): CustomEvent<T, R> = SharedFlowEvent(resultCapture)

/**
 * Creates a [CustomEvent] with a Unit result.
 */
fun <T> createEvent() = createEvent<T, Unit> {}

/**
 * Creates a [SimpleValidatedEvent].
 */
fun <T> createValidatedEvent() = createEvent<SimpleValidated<T>, Boolean> { isValid }

/**
 * Wraps an existing Fabric [Event] into a [Listenable], using [transform] to map recode listeners to its
 * specification.
 */
fun <T, L> wrapFabricEvent(
    event: Event<L>,
    transform: (EventListener<T>) -> L
): WrappedEvent<T, L> {
    return EventWrapper(event, transform)
}

/**
 * A custom [Listenable] event that can be [run]. Event contexts are transformed into "results", and the previous
 * one is stored in [prevResult].
 */
interface CustomEvent<T, R : Any> : Listenable<T> {
    // TODO: events cannot suspend, but is shared mutable state still a problem? do we need StateEvent back?
    val prevResult: R?

    fun run(context: T): R
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

private class SharedFlowEvent<T, R : Any>(private val resultCapture: T.() -> R) : CustomEvent<T, R> {
    private val flow = MutableSharedFlow<T>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override var prevResult: R? = null
        private set

    override fun getNotificationsFrom(module: ExposedModule) = flow

    override fun run(context: T): R {
        check(flow.tryEmit(context)) { "SharedEvent listeners should never suspend" }
        return resultCapture(context)
    }
}

private class EventWrapper<T, L>(
    private val fabricEvent: Event<L>,
    transform: ((T) -> Unit) -> L
) : WrappedEvent<T, L> {
    private val async = createEvent<T>()

    override val invoker: L get() = fabricEvent.invoker()

    init {
        fabricEvent.register(transform(async::run))
    }

    override fun getNotificationsFrom(module: ExposedModule) = async.getNotificationsFrom(module)
}