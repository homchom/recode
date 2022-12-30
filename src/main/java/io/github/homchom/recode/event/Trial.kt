package io.github.homchom.recode.event

import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.nullable
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

suspend inline fun <R : Any> runTrial(crossinline tests: Trial.() -> R) =
    nullable { withContext(Dispatchers.IO) { Trial().tests() } }

class Trial {
    private val enforced = mutableListOf<suspend () -> Unit>()

    suspend inline fun <C, T : Any> NullableScope.testOn(
        event: Hook<C, *>,
        waitDuration: Long,
        crossinline test: (C) -> T?
    ): T {
        return collectOn(event, waitDuration) { test(it.first()) ?: fail() }
    }

    suspend inline fun <C, T : Any> NullableScope.awaitOn(
        event: Hook<C, *>,
        waitDuration: Long,
        crossinline test: (C) -> T?
    ): T {
        return collectOn(event, waitDuration) { it.mapNotNull(test).first() }
    }

    suspend inline fun <C, T : Any> NullableScope.collectOn(
        event: Hook<C, *>,
        duration: Long,
        crossinline collector: suspend (Flow<C>) -> T
    ): T {
        return event.notifications.let { flow ->
            try {
                withTimeout(duration) { collector(flow) }
            } catch (e: TimeoutCancellationException) {
                fail()
            }
        }.also { testEnforced() }
    }

    suspend inline fun <C> NullableScope.testBooleanOn(
        event: Hook<C, *>,
        waitDuration: Long,
        crossinline test: (C) -> Boolean
    ) {
        testOn(event, waitDuration) { test(it).unitOrNull() }
    }

    suspend inline fun <C> NullableScope.awaitBooleanOn(
        event: Hook<C, *>,
        waitDuration: Long,
        crossinline test: (C) -> Boolean
    ) {
        awaitOn(event, waitDuration) { test(it).unitOrNull() }
    }

    suspend fun enforce(block: suspend () -> Unit) {
        block()
        enforced += block
    }

    suspend fun testEnforced() {
        for (rule in enforced) rule()
    }
}