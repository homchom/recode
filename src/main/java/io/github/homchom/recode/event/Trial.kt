package io.github.homchom.recode.event

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend inline fun <R : Any> runTrial(crossinline tests: Trial.() -> R) = try {
    withContext(Dispatchers.IO) { Trial().tests() }
} catch (failure: TrialFailException) {
    null
}

class Trial {
    private val enforced = mutableListOf<suspend () -> Unit>()

    inline fun <T : Any> test(test: () -> T?) = test() ?: fail()

    suspend inline fun <C, T : Any> testOn(
        event: REvent<C, *>,
        duration: Long = 0,
        crossinline test: (C) -> T?
    ): T {
        return event.contextFlow.let { flow ->
            if (duration == 0L) {
                test { test(flow.first()) }
            } else try {
                withTimeout(duration) { flow.mapNotNull { test(it) }.first() }
            } catch (e: CancellationException) {
                fail()
            }
        }.also { testEnforced() }
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