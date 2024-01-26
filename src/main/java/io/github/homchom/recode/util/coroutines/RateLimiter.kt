package io.github.homchom.recode.util.coroutines

import io.github.homchom.recode.util.math.Frequency
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.updateAndGet

/**
 * A simple (client-side) rate limiter. This is necessary in cases where the server
 * has a fixed rate limit, but the client cannot necessarily adhere to it
 * due to [inversion of control](https://en.wikipedia.org/wiki/Inversion_of_control).
 *
 * The rate limiter operates by incrementing [count] at each invocation of [record], triggering a
 * rate limit if `count` is greater than [limit]. `count` is later decremented via [rate].
 *
 * @param rate The asymptotic maximum rate. On this rate's [Frequency.interval], [count] is decremented by
 * [Frequency.occurrences].
 *
 * @see record
 */
class RateLimiter @OptIn(DelicateCoroutinesApi::class) constructor(
    val rate: Frequency,
    val limit: Int = Int.MAX_VALUE,
    private val coroutineScope: CoroutineScope = GlobalScope
) {
    /**
     * A [StateFlow] of the current counter, as determined by [record].
     */
    val count: StateFlow<Int> get() = _count

    private val _count = MutableStateFlow(0)

    /**
     * Records the occurrence of an event that should be rate-limited.
     *
     * @return `false` if the rate
     */
    fun record(): Boolean {
        val currentCount = _count.updateAndGet { it + 1 }

        if (currentCount == 1) try {
            coroutineScope.launch {
                do {
                    delay(rate.interval)
                } while (_count.updateAndGet { it - rate.occurrences } > 0)
            }
        } catch (e: Throwable) { // just in case
            _count.value = 0
            throw e
        }

        return currentCount <= limit
    }
}