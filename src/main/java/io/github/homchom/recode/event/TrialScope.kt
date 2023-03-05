package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.attempt
import io.github.homchom.recode.util.collections.immutable
import io.github.homchom.recode.util.nullable
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlin.time.Duration

suspend fun <R : Any> ExposedModule.trialScope(block: suspend TrialScope.() -> R) =
    nullable {
        withContext(Dispatchers.IO) {
            coroutineScope {
                val trialScope = ConcreteTrialScope(
                    this@trialScope,
                    this@nullable,
                    this@coroutineScope
                )
                trialScope.block().also { coroutineContext.cancelChildren() }
            }
        }
    }

sealed class TrialScope(
    private val module: ExposedModule,
    private val nullableScope: NullableScope,
    val ruleScope: CoroutineScope
) {
    val rules get() = _rules.immutable()
    private val _rules = mutableListOf<() -> Any?>()

    fun <T : Any> enforce(block: () -> T?) {
        _rules += block
    }

    suspend inline fun <C, T : Any> testOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        val result = attempt(attempts) {
            withTimeoutOrNull(timeoutDuration) { test(event.notifications.first()) }
        }
        for (rule in rules) rule() ?: fail()
        return TestResult(result)
    }

    suspend inline fun <C, T : Any> awaitOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        val result = attempt(timeoutDuration) { test(event.notifications.first()) }
        for (rule in rules) rule() ?: fail()
        return TestResult(result)
    }

    suspend inline fun <C, T : Any> enforceOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ) {
        ruleScope.launch {
            while (isActive) { +testOn(event, timeoutDuration = timeoutDuration, test = test) }
        }
    }

    suspend inline fun <C, T : Any> testAndEnforceOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        return testOn(event, attempts, timeoutDuration, test)
            .also { enforceOn(event, timeoutDuration, test) }
    }

    val <C> Listenable<C>.notifications get() = getNotificationsFrom(module)

    suspend fun <T : Any, R : Any> testBy(
        detector: Detector<T, R>,
        input: T?,
        basis: Listenable<*>? = null,
        attempts: UInt = 1u
    ): TestResult<R> {
        return TestResult(detector.checkNextFrom(module, input, basis, attempts))
    }

    suspend fun <T : Any, R : Any> awaitBy(
        detector: Detector<T, R>,
        input: T?,
        basis: Listenable<*>? = null
    ): TestResult<R> {
        return TestResult(detector.detectFrom(module, input, basis))
    }

    suspend fun <T : Any, R : Any> testBy(
        requester: Requester<T, R>,
        input: T,
        isRequest: Boolean = true,
        attempts: UInt = 1u
    ): TestResult<R> {
        return if (isRequest) {
            TestResult(requester.requestNextFrom(module, input, attempts))
        } else {
            testBy(requester, input, null, attempts)
        }
    }

    suspend fun <T : Any, R : Any> awaitBy(
        requester: Requester<T, R>,
        input: T,
        isRequest: Boolean = true
    ): TestResult<R> {
        return if (isRequest) {
            TestResult(requester.requestFrom(module, input))
        } else {
            testBy(requester, input, null)
        }
    }

    

    fun fail(): Nothing = nullableScope.fail()

    suspend inline fun <C> testBooleanOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return testOn(event, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    suspend inline fun <C> enforceBooleanOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ) {
        enforceOn(event, timeoutDuration) { test(it).unitOrNull() }
    }

    suspend inline fun <C> testAndEnforceBooleanOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return testAndEnforceOn(event, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    @JvmInline
    value class TestResult<T : Any>(val value: T?) {
        operator fun invoke() = value
    }

    operator fun <T : Any> TestResult<T>.unaryPlus() = value ?: fail()
}

private class ConcreteTrialScope(
    module: ExposedModule,
    nullableScope: NullableScope,
    ruleScope: CoroutineScope
) : TrialScope(module, nullableScope, ruleScope)