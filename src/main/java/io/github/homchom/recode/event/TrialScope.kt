package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.logError
import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.attempt
import io.github.homchom.recode.util.nullable
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlin.time.Duration

/**
 * Runs [block] in a [TrialScope], where coroutines started in [block] are
 * confined to the lifecycle of [coroutineScope].
 */
fun <R : Any> RModule.trialScope(coroutineScope: CoroutineScope, block: TrialScope.() -> TrialResult<R>?) =
    nullable {
        EnforcerTrialScope(this@trialScope, coroutineScope, this@nullable).block()
    }

/**
 * A wrapper for a [Deferred] [Trial] result.
 */
class TrialResult<T : Any> private constructor(private val deferred: Deferred<T?>) : Deferred<T?> by deferred {
    constructor(instantValue: T?) : this(CompletableDeferred(instantValue))

    constructor(asyncBlock: suspend AsyncTrialScope.() -> T?, module: RModule, scope: CoroutineScope) : this(
        scope.async(Dispatchers.IO) {
            nullable {
                coroutineScope {
                    val trialScope = ConcreteAsyncTrialScope(
                        module,
                        scope,
                        this@nullable,
                        this@coroutineScope
                    )
                    val result = trialScope.asyncBlock() ?: fail()
                    coroutineContext.cancelChildren()
                    result
                }
            }
        }
    )
}

/**
 * A [CoroutineScope] and [NullableScope] that a [Trial] executes in.
 *
 * A trial is a test containing one or more suspension points on events; they are useful for detecting an
 * occurrence that happens in complex steps. TrialScope includes corresponding DSL functions such as [requireTrue]
 * and the suspend functions in [AsyncTrialScope].
 *
 * @see trialScope
 */
sealed interface TrialScope : CoroutineScope {
    val module: RModule

    /**
     * A list of blocking rules that are tested after most trial suspensions, failing the trial on a failed test.
     *
     * @see AsyncTrialScope.sample
     */
    val rules: List<() -> Unit>

    /**
     * Gets this Listenable object's notifications from the module passed to the TrialScope.
     */
    val <T> Listenable<T>.notifications get() = getNotificationsFrom(module)

    /**
     *
     * Takes [count] of this Listenable object's notifications lazily.
     *
     * @see kotlinx.coroutines.flow.take
     */
    fun <T> Listenable<T>.take(count: Int) = notifications.take(count).asListenable()

    /**
     *
     * Transfers [count] of this Listenable object's notifications eagerly into a
     * [kotlinx.coroutines.channels.Channel].
     *
     * @see produceIn
     */
    fun <T> Listenable<T>.channel(count: Int): ReceiveChannel<T>

    /**
     * Gets this Listenable object's notifications eagerly in a coroutine confined to the trial's execution.
     *
     * @see shareIn
     */
    fun <T> Listenable<T>.share(): FlowListenable<T>

    /**
     * Enforces [block] by adding it to [rules].
     */
    fun enforce(block: () -> Unit)

    /**
     * Fails the trial if [predicate] is false.
     */
    fun requireTrue(predicate: Boolean) {
        if (!predicate) fail()
    }

    /**
     * Fails the trial if [predicate] is true.
     */
    fun requireFalse(predicate: Boolean) {
        if (predicate) fail()
    }

    /**
     * Fails this trial.
     *
     * @see NullableScope.fail
     */
    fun fail(): Nothing

    /**
     * Returns an instant [TrialResult] with [value]. Use this when a trial does not end asynchronously.
     */
    fun <R : Any> instant(value: R?) = TrialResult(value)

    /**
     * Returns the asynchronous [TrialResult] of [block] ran in an [AsyncTrialScope],
     * derived from this [TrialScope].
     */
    fun <R : Any> suspending(block: suspend AsyncTrialScope.() -> R?): TrialResult<R>

    /**
     * A shorthand for `unitOrNull().let(::instant)`.
     *
     * @see instant
     * @see unitOrNull
     */
    fun Boolean.instantUnitOrNull() = instant(unitOrNull())
}

/**
 * A [TrialScope] with suspending DSL functions such as [sample] and [sampleBy].
 */
sealed class AsyncTrialScope(
    module: RModule,
    coroutineScope: CoroutineScope,
    nullableScope: NullableScope
) : TrialScope by EnforcerTrialScope(module, coroutineScope, nullableScope) {
    abstract val ruleScope: CoroutineScope

    /**
     * Tests [test] on the invocations of [event] until a non-null result is returned. To test only some
     * invocations (e.g. the first), use a function like [TrialScope.take] or [TrialScope.channel].
     *
     * @see TestResult
     */
    suspend inline fun <C, T : Any> await(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        val result = withTimeoutOrNull(timeoutDuration) {
            event.notifications.mapNotNull(test).firstOrNull()
        }
        for (rule in rules) rule()
        return TestResult(result)
    }

    /**
     * Tests [test] on the first [attempts] values of [channel] until a non-null result is returned.
     *
     * @throws kotlinx.coroutines.channels.ClosedReceiveChannelException
     * if [channel] closes while still attempting.
     *
     * @see TestResult
     */
    suspend inline fun <C, T : Any> sample(
        channel: ReceiveChannel<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ): TestResult<T> {
        val result = withTimeoutOrNull(timeoutDuration) {
            attempt(attempts) { test(channel.receive()) }
        }
        for (rule in rules) rule()
        return TestResult(result)
    }

    /**
     * Enforces [test] on each invocation of [event], failing the trial on a failed test.
     *
     * @see TrialScope.enforce
     */
    suspend inline fun <C, T : Any> enforce(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> T?
    ) {
        ruleScope.launch {
            while (isActive) { +await(event.take(1), timeoutDuration, test) }
        }
    }

    /**
     * Awaits a detected result from [detector] on the invocation of [basis].
     *
     * @see Detector.detectFrom
     * @see TestResult
     */
    @ExperimentalCoroutinesApi
    suspend fun <T : Any, R : Any> awaitBy(
        detector: Detector<T, R>,
        input: T?,
        basis: Listenable<*>? = null
    ): TestResult<R> {
        return TestResult(detector.detectFrom(module, input, basis))
            .also { for (rule in rules) rule() }
    }

    /**
     * Tests [detector] by checking the next invocation of [basis].
     *
     * @see Detector.checkNextFrom
     * @see TestResult
     */
    @ExperimentalCoroutinesApi
    suspend fun <T : Any, R : Any> sampleBy(
        detector: Detector<T, R>,
        input: T?,
        basis: Listenable<*>? = null,
        attempts: UInt = 1u
    ): TestResult<R> {
        return TestResult(detector.checkNextFrom(module, input, basis, attempts))
            .also { for (rule in rules) rule() }
    }

    /**
     * Awaits a requested (if [isRequest] is true) or detected (otherwise) result from [requester].
     *
     * @see Requester.requestFrom
     * @see TestResult
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun <T : Any, R : Any> awaitBy(
        requester: Requester<T, R>,
        input: T,
        isRequest: Boolean = true
    ): TestResult<R> {
        return if (isRequest) {
            val result = try {
                requester.requestFrom(module, input)
                    .also { for (rule in rules) rule() }
            } catch (e: RequestTrialException) {
                logError(e.localizedMessage)
                null
            }
            TestResult(result)
        } else {
            awaitBy(requester, input, null)
        }
    }

    /**
     * Tests [test] on the invocations of [event] until a true result is returned.
     *
     * @see await
     */
    suspend inline fun <C> awaitBoolean(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return await(event, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * Tests [test] on the first [attempts] values of [channel] until a true result is returned.
     *
     * @throws kotlinx.coroutines.channels.ClosedReceiveChannelException
     * if [channel] closes while still attempting.
     *
     * @see test
     */
    suspend inline fun <C> sampleBoolean(
        channel: ReceiveChannel<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return sample(channel, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * @see enforce
     */
    suspend inline fun <C> enforceBoolean(
        event: Listenable<C>,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ) {
        enforce(event, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * A result from a suspending test. To require a passing (non-null) result or fail the trial,
     * prepend it with [unaryPlus].
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

private class EnforcerTrialScope(
    override val module: RModule,
    private val coroutineScope: CoroutineScope,
    private val nullableScope: NullableScope
) : TrialScope, CoroutineScope by coroutineScope {
    override val rules get() = _rules

    private val _rules = mutableListOf<() -> Unit>()

    override fun <T> Listenable<T>.channel(count: Int) = notifications
        .take(count)
        .buffer(count)
        .produceIn(coroutineScope)

    override fun <T> Listenable<T>.share() = notifications
        .shareIn(coroutineScope, SharingStarted.Eagerly)
        .asListenable()


    override fun enforce(block: () -> Unit) {
        block()
        rules += _rules
    }

    override fun <R : Any> suspending(block: suspend AsyncTrialScope.() -> R?) =
        TrialResult(block, module, coroutineScope)

    override fun fail(): Nothing = nullableScope.fail()
}

private class ConcreteAsyncTrialScope(
    module: RModule,
    coroutineScope: CoroutineScope,
    nullableScope: NullableScope,
    override val ruleScope: CoroutineScope
) : AsyncTrialScope(module, coroutineScope, nullableScope)