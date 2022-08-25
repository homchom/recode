package io.github.homchom.recode.event

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.seconds

suspend inline fun <R : Any> runTrial(crossinline tests: Trial.() -> R) = try {
    withContext(Dispatchers.IO) { Trial().tests() }
} catch (failure: TrialFailException) {
    null
}

class Trial {
    private val enforced = mutableListOf<suspend () -> Unit>()

    inline fun <T : Any> test(test: () -> T?) = test() ?: fail()

    suspend inline fun <C, T : Any> testOn(event: REvent<C, *>, crossinline test: (C) -> T?) =
        try {
            // TODO: make timeout duration configurable
            withTimeout(90.seconds) {
                event.contextFlow.mapNotNull { test(it) }.first()
            }.also { testEnforced() }
        } catch (e: CancellationException) {
            fail()
        }

    suspend fun enforce(block: suspend () -> Unit) {
        block()
        enforced += block
    }

    suspend fun testEnforced() {
        for (rule in enforced) rule()
    }

    fun fail(): Nothing = throw TrialFailException()
}

inline fun testBoolean(test: () -> Boolean) = if (test()) Unit else null

private class TrialFailException : Exception()