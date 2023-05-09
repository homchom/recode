package io.github.homchom.recode.util

import kotlinx.coroutines.isActive
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

inline fun <R : Any> attempt(attempts: UInt, block: (UInt) -> R?): R? {
    for (index in 1u..attempts) {
        block(index)?.let { return it }
    }
    return null
}

suspend inline fun <R : Any> attempt(timeoutDuration: Duration, crossinline block: suspend () -> R?): R? {
    return withTimeoutOrNull(timeoutDuration) main@{
        while (isActive) {
            block()?.let { return@main it }
        }
        null
    }
}