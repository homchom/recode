package io.github.homchom.recode.event.trial

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.util.lib.unitOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.produceIn
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

/**
 * A [CoroutineScope] that a [Trial] executes in.
 *
 * A trial is a test containing one or more suspension points on events; they are useful for detecting
 * an occurrence that happens in complex steps. TrialScope includes corresponding DSL functions such as
 * [add] and [test].
 *
 * @param hidden To be used by trials to invalidate "notification-like" intermediate event contexts.
 *
 * @see trial
 */
class TrialScope @DelicateCoroutinesApi constructor(
    val coroutineScope: CoroutineScope,
    val hidden: Boolean = false,
) {
    /**
     * A list of blocking rules that are tested after most trial suspensions, failing the trial on a failed test.
     *
     * @see test
     */
    val rules: List<() -> Unit> get() = _rules

    private val _rules = mutableListOf<() -> Unit>()

    /**
     * An alias for [UInt.MAX_VALUE], used when a test should run as long as possible (in an awaiting fashion).
     */
    inline val unlimited get() = UInt.MAX_VALUE

    /**
     * Transfers this context flow eagerly into a [kotlinx.coroutines.channels.Channel], allowing it to be used
     * by the Trial in a suspending manner.
     */
    fun <T> Flow<T>.add() = buffer(Channel.UNLIMITED).produceIn(coroutineScope)

    /**
     * Transfers this context flow eagerly into a concurrent [Queue], allowing it to be used by the Trial if
     * present when needed.
     *
     * Note that, due to concurrent collection design, this function requires [T] be non-nullable. If a flow
     * with nullable context is desired for use, map it into a flow of [io.github.homchom.recode.util.Case]
     * objects before calling.
     */
    fun <T : Any> Flow<T>.addOptional(): Queue<T> {
        val queue = ConcurrentLinkedQueue<T>()
        coroutineScope.launch {
            collect { queue += it }
        }
        return queue
    }

    /**
     * @see Flow.add
     */
    fun <T> Listenable<T>.add() = notifications.add()

    /**
     * @see Flow.add
     */
    fun <T : Any> Listenable<T>.addOptional() = notifications.addOptional()

    /**
     * Enforces [rule] by adding it to [rules].
     */
    fun enforce(rule: () -> Unit) {
        rule()
        _rules += rule
    }

    /**
     * Tests [test] on the first [attempts] values of [channel] until a non-null result is returned.
     *
     * @throws kotlinx.coroutines.channels.ClosedReceiveChannelException
     * if [channel] closes while still attempting.
     *
     * @see add
     */
    suspend inline fun <C, T : Any> test(
        channel: ReceiveChannel<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        crossinline test: suspend (C) -> T?
    ): T? {
        val result = withTimeoutOrNull(timeoutDuration) {
            (1u..attempts).firstNotNullOfOrNull { test(channel.receive()) }
        }
        for (rule in rules) rule()
        return result
    }

    /**
     * Asynchronously enforces [test] on the remaining elements of [channel], consuming the channel and failing
     * the trial on a failed test. Also yields (suspends) for one iteration of Minecraft's event loop so [channel]
     * is up-to-date.
     *
     * @see TrialScope.enforce
     */
    suspend inline fun <C, T : Any> enforce(
        channel: ReceiveChannel<C>,
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        crossinline test: (C) -> T?
    ) {
        coroutineScope.launch(coroutineContext, CoroutineStart.UNDISPATCHED) {
            channel.consumeEach { test(it) ?: throw TrialScopeException() }
        }
        yield() // fast-fail
    }

    /**
     * Fails the trial if any elements in [channel] are received.
     *
     * @see enforce
     */
    suspend fun <C> failOn(channel: ReceiveChannel<C>) = enforce(channel, Dispatchers.Default) { null }

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
    ): Unit? {
        return test(channel, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * @see failOn
     */
    suspend inline fun <C> enforceBoolean(channel: ReceiveChannel<C>, crossinline test: (C) -> Boolean) {
        enforce(channel) { test(it).unitOrNull() }
    }

    /**
     * @return an instant [TrialResult] with [value]. Use this when a trial does not end asynchronously.
     */
    fun <R : Any> instant(value: R?) = TrialResult(value)

    /**
     * @return the asynchronous [TrialResult] of [block] ran in its own [TrialScope].
     */
    fun <R : Any> suspending(block: suspend TrialScope.() -> R?) =
        TrialResult(block, coroutineScope, hidden)

    /**
     * A shorthand for `unitOrNull().let(::instant)`.
     *
     * @see instant
     * @see unitOrNull
     */
    fun Boolean.instantUnitOrNull() = instant(unitOrNull())
}

/**
 * An exceptional return in a [TrialScope], such as when [TrialScope.enforce] fails. This can be safely thrown
 * from inside the tests of a [trial], but is expensive; a `return` is almost always preferable.
 */
class TrialScopeException : RuntimeException()