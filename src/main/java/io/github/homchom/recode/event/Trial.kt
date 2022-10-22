package io.github.homchom.recode.event

import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.nullable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend inline fun <R : Any> runTrial(crossinline tests: Trial.() -> R) =
    nullable { withContext(Dispatchers.IO) { Trial().tests() } }

class Trial {
    private val enforced = mutableListOf<suspend () -> Unit>()

    inline fun <T : Any> NullableScope.test(test: () -> T?) = test() ?: fail()

    inline fun NullableScope.testBoolean(test: () -> Boolean) {
        test { if (test()) Unit else null }
    }

    suspend inline fun <C, T : Any> NullableScope.testOn(
        event: REvent<C, *>,
        duration: Long = 0,
        crossinline test: (C) -> T?
    ): T {
        return event.contextFlow.let { flow ->
            if (duration == 0L) {
                test { test(flow.first()) }
            } else try {
                withTimeout(duration) { flow.mapNotNull { test(it) }.first() }
            } catch (e: TimeoutCancellationException) {
                fail()
            }
        }.also { testEnforced() }
    }

    suspend inline fun <C> NullableScope.testBooleanOn(
        event: REvent<C, *>,
        duration: Long = 0,
        crossinline test: (C) -> Boolean
    ) {
        testOn(event, duration) { if (test(it)) Unit else null }
    }

    suspend fun enforce(block: suspend () -> Unit) {
        block()
        enforced += block
    }

    suspend fun testEnforced() {
        for (rule in enforced) rule()
    }
}