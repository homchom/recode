package io.github.homchom.recode.event.trial

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.util.coroutines.withNotifications
import io.github.homchom.recode.util.std.unitOrNull
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.produceIn
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration

/**
 * Runs a *non-suspending* [block] in a [TrialScope]. This is more internal than [trial] and
 * should not be called from general code.
 */
@DelicateCoroutinesApi
fun <R : Any> CoroutineScope.nonSuspendingTrialScope(hidden: Boolean, block: TrialScope.() -> R?) =
    try {
        TrialCoroutineScope(this, hidden).block()
    } catch (e: TrialScopeException) {
        null
    }

/**
 * Runs a *suspending* [block] in a [TrialScope]. This is more internal than [trial] and
 * should not be called from general code.
 */
@DelicateCoroutinesApi
suspend fun <R : Any> suspendingTrialScope(hidden: Boolean, block: suspend TrialScope.() -> R?) =
    try {
        coroutineScope {
            val trialScope = TrialCoroutineScope(this, hidden)
            val interceptor = coroutineContext[ContinuationInterceptor]!!
                .withNotifications { for (rule in trialScope.rules) rule() }

            yield() // give channels at least one opportunity to send

            withContext(interceptor) { trialScope.block() }
                .also { coroutineContext.cancelChildren() }
        }
    } catch (e: TrialScopeException) {
        null
    }

/**
 * A [CoroutineScope] that a [Trial] executes in.
 *
 * A trial is a test containing one or more suspension points on events; they are useful for detecting
 * an occurrence that happens in complex steps. TrialScope includes corresponding DSL functions such as
 * [add] and [test].
 *
 * @property hidden To be used by trials to invalidate "notification-like" intermediate event contexts.
 *
 * @see trial
 */
sealed interface TrialScope {
    val hidden: Boolean

    /**
     * An alias for [UInt.MAX_VALUE], used when a test should run as long as possible (in an awaiting fashion).
     */
    val unlimited get() = UInt.MAX_VALUE

    /**
     * Transfers this context flow eagerly into a [kotlinx.coroutines.channels.Channel], allowing it to be used
     * by the Trial in a suspending manner.
     */
    fun <T> Flow<T>.add(): ReceiveChannel<T>

    /**
     * Transfers this context flow eagerly into a concurrent [Queue], allowing it to be used by the Trial if
     * present when needed.
     *
     * Note that, due to concurrent collection design, this function requires [T] be non-nullable. If a flow
     * with nullable context is desired for use, map it into a flow of [io.github.homchom.recode.util.Case]
     * objects before calling.
     */
    fun <T : Any> Flow<T>.addOptional(): Queue<T>

    /**
     * @see Flow.add
     */
    fun <T> Listenable<T>.add() = notifications.add()

    /**
     * @see Flow.add
     */
    fun <T : Any> Listenable<T>.addOptional() = notifications.addOptional()

    /**
     * Enforces [rule] by invoking it after every suspension point.
     */
    fun enforce(rule: () -> Unit)

    /**
     * Tests [test] on the first [attempts] values of [channel] until a non-null result is returned.
     *
     * @throws kotlinx.coroutines.channels.ClosedReceiveChannelException
     * if [channel] closes while still attempting.
     *
     * @see add
     */
    suspend fun <C, T : Any> test(
        channel: ReceiveChannel<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        test: suspend (C) -> T?
    ): T? {
        return withTimeoutOrNull(timeoutDuration) {
            (1u..attempts).firstNotNullOfOrNull { test(channel.receive()) }
        }
    }

    /**
     * Asynchronously enforces [test] on the remaining elements of [channel], consuming the channel and
     * failing the trial on a failed test. Also yields (suspends) for one iteration of Minecraft's event
     * loop so [channel] is up-to-date.
     *
     * @see TrialScope.enforce
     */
    suspend fun <C, T : Any> enforce(
        channel: ReceiveChannel<C>,
        coroutineContext: CoroutineContext = EmptyCoroutineContext,
        test: (C) -> T?
    )

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
    suspend fun <C> testBoolean(
        channel: ReceiveChannel<C>,
        attempts: UInt = 1u,
        timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
        test: (C) -> Boolean
    ): Unit? {
        return test(channel, attempts, timeoutDuration) { test(it).unitOrNull() }
    }

    /**
     * @see failOn
     */
    suspend fun <C> enforceBoolean(channel: ReceiveChannel<C>, test: (C) -> Boolean) {
        enforce(channel) { test(it).unitOrNull() }
    }

    /**
     * @return an instant [TrialResult] with [value]. Use this when a trial does not end asynchronously.
     */
    fun <R : Any> instant(value: R?): TrialResult<R>

    /**
     * @return the asynchronous [TrialResult] of [block] ran in its own [TrialScope].
     */
    fun <R : Any> suspending(block: suspend TrialScope.() -> R?): TrialResult<R>

    /**
     * A shortcut for `unitOrNull().let(::instant)`.
     *
     * @see instant
     * @see unitOrNull
     */
    fun Boolean.instantUnitOrNull() = instant(unitOrNull())
}

private class TrialCoroutineScope(
    private val coroutineScope: CoroutineScope,
    override val hidden: Boolean = false
) : TrialScope, CoroutineScope by coroutineScope {
    val rules = mutableListOf<() -> Unit>()

    override fun <T> Flow<T>.add() = buffer(Channel.UNLIMITED).produceIn(coroutineScope)

    override fun <T : Any> Flow<T>.addOptional(): Queue<T> {
        val queue = ConcurrentLinkedQueue<T>()
        coroutineScope.launch {
            collect { queue += it }
        }
        return queue
    }

    override fun enforce(rule: () -> Unit) {
        rule()
        rules += rule
    }

    override suspend fun <C, T : Any> enforce(
        channel: ReceiveChannel<C>,
        coroutineContext: CoroutineContext,
        test: (C) -> T?
    ) {
        coroutineScope.launch(coroutineContext, CoroutineStart.UNDISPATCHED) {
            channel.consumeEach { test(it) ?: throw TrialScopeException() }
        }
        yield() // fast fail
    }

    override fun <R : Any> instant(value: R?) = TrialResult(value)

    override fun <R : Any> suspending(block: suspend TrialScope.() -> R?) =
        TrialResult(block, coroutineScope, hidden)
}

/**
 * An exceptional return in a [TrialScope], such as when [TrialScope.enforce] fails. This can be safely thrown
 * from inside the tests of a [trial], but is expensive; a `return` is almost always preferable.
 */
class TrialScopeException : RuntimeException()