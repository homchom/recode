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

/**
 * Runs [block] in a [TrialScope].
 */
suspend fun <R : Any> ExposedModule.trialScope(block: suspend TrialScope.() -> Deferred<R?>?) =
    nullable {
        withContext(Dispatchers.IO) {
            coroutineScope {
                val trialScope = ConcreteTrialScope(
                    this@trialScope,
                    this@nullable,
                    this@coroutineScope
                )
                val result = trialScope.block() ?: fail()
                coroutineContext.cancelChildren()
                result
            }
        }
    }

/**
 * A scope that a trial executes in.
 *
 * A trial is a test containing one or more suspension points on events; they are useful for detecting an
 * occurrence that happens in complex steps. TrialScope includes corresponding DSL functions such as [testOn]
 * and [testBy].
 */
sealed class TrialScope(
    private val module: ExposedModule,
    private val nullableScope: NullableScope,
    val ruleScope: CoroutineScope
) : CoroutineScope by module {
    /**
     * A list of blocking rules that are tested after most trial suspensions, failing the trial on a failed test.
     *
     * @see testOn
     */
    val rules get() = _rules.immutable()

    private val _rules = mutableListOf<() -> Any?>()

    /**
     * Fails the trial if [predicate] is false.
     */
    fun requireTrue(predicate: Boolean) {
        if (!predicate) fail()
    }

    /**
     * Enforces [block] by adding it to [rules].
     */
    fun enforce(block: () -> Unit) {
        block()
        _rules += block
    }

    /**
     * Tests [test] on the next invocation of [event], where a null result is a failed test and a non-null result
     * is a passed test.
     *
     * @see TestResult
     */
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

    /**
     * Tests [test] on the invocations of [event] until a non-null result is returned.
     *
     * @see TestResult
     */
    suspend inline fun <C, T : Any> awaitOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        val result = attempt(timeoutDuration) { test(event.notifications.first()) }
        for (rule in rules) rule() ?: fail()
        return TestResult(result)
    }

    /**
     * Enforces [test] on each invocation of [event], failing the trial on a failed test (a null result).
     *
     * @see enforce
     */
    suspend inline fun <C, T : Any> enforceOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ) {
        ruleScope.launch {
            while (isActive) { +testOn(event, timeoutDuration = timeoutDuration, test = test) }
        }
    }

    /**
     * @see testOn
     * @see enforceOn
     */
    suspend inline fun <C, T : Any> testAndEnforceOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        return testOn(event, attempts, timeoutDuration, test)
            .also { enforceOn(event, timeoutDuration, test) }
    }

    /**
     * Gets this Listenable object's notifications from the module passed to the TrialScope.
     */
    val <C> Listenable<C>.notifications get() = getNotificationsFrom(module)

    /**
     * Tests [detector] by checking the next invocation of [basis].
     *
     * @see Detector.checkNextFrom
     * @see TestResult
     */
    suspend fun <T : Any, R : Any> testBy(
        detector: Detector<T, R>,
        input: T?,
        basis: Listenable<*>? = null,
        attempts: UInt = 1u
    ): TestResult<R> {
        return TestResult(detector.checkNextFrom(module, input, basis, attempts))
    }

    /**
     * Awaits a detected result from [detector] on the invocation of [basis].
     *
     * @see Detector.detectFrom
     * @see TestResult
     */
    suspend fun <T : Any, R : Any> awaitBy(
        detector: Detector<T, R>,
        input: T?,
        basis: Listenable<*>? = null
    ): TestResult<R> {
        return TestResult(detector.detectFrom(module, input, basis))
    }

    /**
     * Requests and tests [requester] by checking the next invocation of its primary basis, returning the
     * request result.
     *
     * @see Requester.requestNextFrom
     * @see TestResult
     */
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

    /**
     * Awaits a requested result from [requester].
     *
     * @see Requester.requestFrom
     * @see TestResult
     */
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

    /**
     * Tests [test] on the next invocation of [event], where the test returns whether the test passed.
     *
     * @see testOn
     */
    suspend inline fun <C> testBooleanOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return testOn(event, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * Enforces [test] on each invocation of [event], failing the trial on a failed test (one that returned false).
     *
     * @see enforce
     */
    suspend inline fun <C> enforceBooleanOn(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ) {
        enforceOn(event, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * @see testBooleanOn
     * @see enforceBooleanOn
     */
    suspend inline fun <C> testAndEnforceBooleanOn(
        event: Listenable<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return testAndEnforceOn(event, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * Fails this trial.
     *
     * @see NullableScope.fail
     */
    fun fail(): Nothing = nullableScope.fail()

    /**
     * Creates and returns an instant [Deferred] result with [value].
     * Use this when a trial does not end asynchronously.
     *
     * @see CompletableDeferred
     */
    fun <R> instant(value: R) = CompletableDeferred(value)

    /**
     * A shorthand for `unitOrNull().let(::instant)`.
     *
     * @see instant
     * @see unitOrNull
     */
    fun Boolean.instantUnitOrNull() = instant(unitOrNull())

    /**
     * A result from a suspending test. To require a passing result or fail the trial, prepend it with [unaryPlus].
     */
    @JvmInline
    value class TestResult<T : Any>(val value: T?) {
        /**
         * Unboxes the result, returning [value].
         */
        operator fun invoke() = value
    }

    /**
     * Returns a non-null [TestResult.value] or fails the trial.
     */
    operator fun <T : Any> TestResult<T>.unaryPlus() = value ?: fail()
}

private class ConcreteTrialScope(
    module: ExposedModule,
    nullableScope: NullableScope,
    ruleScope: CoroutineScope
) : TrialScope(module, nullableScope, ruleScope)