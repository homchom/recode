package io.github.homchom.recode.event

import com.google.common.cache.CacheBuilder
import io.github.homchom.recode.PowerSink
import io.github.homchom.recode.game.currentTick
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

// TODO: replace stableInterval with a tick utility
// TODO: reconcile ticks with 1.20 tick rate

/**
 * Creates a [BufferedCustomEvent] that runs asynchronously and caches the result. [BufferedCustomEvent.stabilize]
 * should be called once during each critical section of this event's execution; if this is done, the result will
 * be cached roughly on [stableTickInterval] (in ticks).
 *
 * @param keySelector A function that transforms [I] into a hashable key type [K] for the buffer.
 * @param contextGenerator A function that transforms [I] into the context type [T] when needed.
 * @param cacheDuration How long event contexts should stay in the cache after being written before eviction.
 */
fun <T, R : Any, I, K : Any> createBufferedEvent(
    resultCapture: (T) -> R,
    stableTickInterval: Int,
    keySelector: (I) -> K,
    contextGenerator: (I) -> T,
    cacheDuration: Duration = 1.seconds
): BufferedCustomEvent<T, R, I> {
    val delegate = createEvent(resultCapture)
    return BufferedFlowEvent(delegate, stableTickInterval, keySelector, contextGenerator, cacheDuration)
}

/**
 * A custom, buffered [ResultListenable] event that can be [run]. Event contexts are supplied and transformed
 * into results; the event runs asynchronously and caches the result on some interval, and the most recent result
 * is stored in [previous].
 *
 * @param I The event's input type, which is mapped to [T] only when needed.
 */
interface BufferedCustomEvent<T, R, I> : ResultListenable<T, R?> {
    fun run(input: I): R

    fun stabilize()
}

private class BufferedFlowEvent<T, R : Any, I, K : Any>(
    private val delegate: CustomEvent<T, R>,
    stableTickInterval: Int,
    private val keySelector: (I) -> K,
    private val contextGenerator: (I) -> T,
    cacheDuration: Duration = 1.seconds
) : BufferedCustomEvent<T, R, I>, PowerSink by delegate {
    override val notifications by delegate::notifications

    // TODO: use Caffeine (official successor) or other alternative?
    private val buffer = CacheBuilder.newBuilder()
        .expireAfterAccess(cacheDuration.toJavaDuration())
        .build<K, R>()

    private val stabilizer = object {
        var passes = 1
            private set

        var passIndex = -1
        var runIndex = 0

        private var prevTick = 0L

        fun stamp() {
            val now = currentTick
            if (++passIndex == passes) {
                val elapsed = (now - prevTick).toInt().coerceAtLeast(1)
                passes = stableTickInterval / elapsed
                passIndex = 0
            }
            runIndex = passIndex
            prevTick = now
        }
    }

    override val previous = MutableStateFlow<R?>(null)

    override fun run(input: I): R {
        val key = keySelector(input)
        val bufferResult = buffer.getIfPresent(key)
        val result = if (bufferResult == null) {
            delegate.run(contextGenerator(input)).also { buffer.put(key, it) }
        } else {
            if (++stabilizer.runIndex >= stabilizer.passes) {
                buffer.put(key, delegate.run(contextGenerator(input)))
                stabilizer.runIndex = 0
            }
            bufferResult
        }
        previous.value = result
        return result
    }

    override fun stabilize() = stabilizer.stamp()
}