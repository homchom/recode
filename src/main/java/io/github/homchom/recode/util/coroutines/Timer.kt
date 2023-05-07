package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

/**
 * Returns an infinite timer [kotlinx.coroutines.flow.Flow] with [period] and [initialDelay].
 */
fun timer(period: Duration, initialDelay: Duration = period) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}