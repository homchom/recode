package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

class TrialScope(val module: ExposedModule, private val nullableScope: NullableScope) {
    private val enforced = mutableListOf<suspend () -> Unit>()

    fun <T : Any> test(value: T?) = testBoolean(value != null)

    fun testBoolean(value: Boolean) = value.also { if (!it) fail() }

    suspend inline fun <C, T : Any> testOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): T {
        return collectOn(event, timeoutDuration) { test(it.first()) ?: fail() }
    }

    suspend inline fun <C, T : Any> awaitOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): T {
        return collectOn(event, timeoutDuration) { it.mapNotNull(test).first() }
    }

    suspend inline fun <C, T : Any> collectOn(
        event: Listenable<C>,
        timeoutDuration: Duration,
        crossinline collector: suspend (Flow<C>) -> T
    ): T {
        return event.getNotificationsFrom(module).let { flow ->
            withTimeoutOrNull(timeoutDuration) { collector(flow) } ?: fail()
        }.also { runEnforced() }
    }

    suspend inline fun <C> testBooleanOn(
        event: Listenable<C>,
        timeoutDuration: Duration,
        crossinline test: (C) -> Boolean
    ) {
        testOn(event, timeoutDuration) { test(it).unitOrNull() }
    }

    suspend inline fun <C> awaitBooleanOn(
        event: Listenable<C>,
        timeoutDuration: Duration,
        crossinline test: (C) -> Boolean
    ) {
        awaitOn(event, timeoutDuration) { test(it).unitOrNull() }
    }

    suspend fun <T : Any, R : Any> subTrial(detector: Detector<T, R>, input: T?) =
        detector.detect(input) ?: fail()

    suspend fun <T : Any, R : Any> subTrial(requester: Requester<T, R>, input: T, isRequest: Boolean) =
        with(requester) { if (isRequest) request(input) else subTrial(this, input) }

    suspend fun enforce(block: suspend () -> Unit) {
        block()
        enforced += block
    }

    suspend fun runEnforced() {
        for (rule in enforced) rule()
    }

    fun fail(): Nothing = nullableScope.fail()
}