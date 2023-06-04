package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.CoroutineModule
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.util.NullableScope
import io.github.homchom.recode.util.attempt
import io.github.homchom.recode.util.nullable
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.take
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
                    val result = try {
                        trialScope.asyncBlock() ?: fail()
                    } catch (e: RequestTimeoutException) {
                        fail()
                    }
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
sealed interface TrialScope : CoroutineModule {
    /**
     * A list of blocking rules that are tested after most trial suspensions, failing the trial on a failed test.
     *
     * @see AsyncTrialScope.test
     */
    val rules: List<() -> Unit>

    /**
     * Transfers [amount] of this Listenable object's notifications eagerly into a
     * [kotlinx.coroutines.channels.Channel].
     *
     * @see produceIn
     * @see AsyncTrialScope.test
     */
    fun <T> Listenable<T>.next(amount: Int = 1): ReceiveChannel<T>

    /**
     * Transfers this Listenable object's notifications eagerly into a
     * [kotlinx.coroutines.channels.Channel] without limit.
     *
     * @see next
     */
    fun <T> Listenable<T>.channel() = next(Int.MAX_VALUE)

    /**
     * @see asListenable
     * @see Listenable.next
     */
    fun <T> Flow<T>.next(amount: Int = 1) = asListenable().next(amount)

    /**
     * @see asListenable
     * @see Listenable.channel
     */
    fun <T> Flow<T>.channel() = asListenable().channel()

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
 * A [TrialScope] with suspending DSL functions, mainly [next] and [test].
 */
sealed class AsyncTrialScope(
    module: RModule,
    coroutineScope: CoroutineScope,
    nullableScope: NullableScope
) : TrialScope by EnforcerTrialScope(module, coroutineScope, nullableScope) {
    abstract val ruleScope: CoroutineScope

    /**
     * Tests [test] on the first [attempts] values of [channel] until a non-null result is returned.
     *
     * @throws kotlinx.coroutines.channels.ClosedReceiveChannelException
     * if [channel] closes while still attempting.
     *
     * @see next
     * @see TestResult
     */
    suspend inline fun <C, T : Any> test(
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
     * Enforces [test] on the remaining elements of [channel], consuming the channel and failing the trial
     * on a failed test.
     *
     * @see TrialScope.enforce
     */
    inline fun <C, T : Any> enforce(channel: ReceiveChannel<C>, crossinline test: (C) -> T?) {
        ruleScope.launch {
            channel.consumeEach { test(it)!! }
        }
    }

    /**
     * Fails the trial if any elements in [channel] are received.
     *
     * @see enforce
     */
    fun <C> failOn(channel: ReceiveChannel<C>) = enforce(channel) { null }

    /**
     * Tests [test] on the first [attempts] values of [channel] until a true result is returned.
     *
     * @throws kotlinx.coroutines.channels.ClosedReceiveChannelException
     * if [channel] closes while still attempting.
     *
     * @see test
     */
    suspend inline fun <C> testBoolean(
        channel: ReceiveChannel<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: (C) -> Boolean
    ): TestResult<Unit> {
        return test(channel, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * @see failOn
     */
    inline fun <C> enforceBoolean(channel: Channel<C>, crossinline test: (C) -> Boolean) {
        enforce(channel) { test(it).unitOrNull() }
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
    module: RModule,
    private val coroutineScope: CoroutineScope,
    private val nullableScope: NullableScope
) : TrialScope, RModule by module {
    // https://youtrack.jetbrains.com/issue/KT-59051/
    override val coroutineContext get() = coroutineScope.coroutineContext

    override val rules get() = _rules

    private val _rules = mutableListOf<() -> Unit>()

    override fun <T> Listenable<T>.next(amount: Int) = notifications
        .take(amount)
        .buffer(amount)
        .produceIn(coroutineScope)

    override fun enforce(block: () -> Unit) {
        block()
        rules += _rules
    }

    override fun <R : Any> suspending(block: suspend AsyncTrialScope.() -> R?) =
        TrialResult(block, this, coroutineScope)

    override fun fail(): Nothing = nullableScope.fail()
}

private class ConcreteAsyncTrialScope(
    module: RModule,
    coroutineScope: CoroutineScope,
    nullableScope: NullableScope,
    override val ruleScope: CoroutineScope
) : AsyncTrialScope(module, coroutineScope, nullableScope)