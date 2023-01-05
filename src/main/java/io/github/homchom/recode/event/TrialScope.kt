package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.util.BreaksControlFlow
import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

@BreaksControlFlow
class TrialScope(nullableScope: NullableScope, val isRequest: Boolean) : NullableScope by nullableScope {
    private val enforced = mutableListOf<suspend () -> Unit>()

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
        return event.notifications.let { flow ->
            try {
                withTimeout(timeoutDuration) { collector(flow) }
            } catch (e: TimeoutCancellationException) {
                fail()
            }
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

    suspend fun <T : Any> subTrial(detector: Detector<T>) = detector.detect() ?: fail()

    suspend fun <T : Any> subTrial(requester: Requester<T>) = with(requester) {
        if (isRequest) request() else subTrial(this as Detector<T>)
    }

    suspend fun enforce(block: suspend () -> Unit) {
        block()
        enforced += block
    }

    suspend fun runEnforced() {
        for (rule in enforced) rule()
    }
}