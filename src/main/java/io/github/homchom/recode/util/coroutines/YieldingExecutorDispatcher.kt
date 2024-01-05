package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.*
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * Constructs a [YieldingExecutorDispatcher] with a shared (yielding) [executor], using [immediatePredicate]
 * to determine whether dispatched [Runnable] blocks should be executed immediately.
 */
inline fun YieldingExecutorDispatcher(executor: Executor, crossinline immediatePredicate: () -> Boolean) =
    YieldingExecutorDispatcher(
        { block ->
            if (immediatePredicate()) block.run() else executor.execute(block)
        },
        executor
    )

/**
 * Constructs a [YieldingExecutorDispatcher] with a shared [executor] that should always yield.
 */
fun YieldingExecutorDispatcher(executor: Executor) = YieldingExecutorDispatcher(executor) { false }

/**
 * A [CoroutineDispatcher] derived from two [Executor]s to support [yield]ing.
 *
 * @param defaultExecutor The executor used by [dispatch].
 * @param yieldingExecutor The executor used by [dispatchYield].
 *
 * @see asCoroutineDispatcher
 */
@OptIn(InternalCoroutinesApi::class)
class YieldingExecutorDispatcher(
    defaultExecutor: Executor,
    yieldingExecutor: Executor
) : CoroutineDispatcher(), Delay {
    private val defaultDelegate = defaultExecutor.asCoroutineDispatcher()
    private val yieldingDelegate = yieldingExecutor.asCoroutineDispatcher()
    private val delay get() = defaultDelegate as Delay

    override fun dispatch(context: CoroutineContext, block: Runnable) =
        defaultDelegate.dispatch(context, block)

    override fun dispatchYield(context: CoroutineContext, block: Runnable) =
        yieldingDelegate.dispatchYield(context, block)

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) =
        delay.scheduleResumeAfterDelay(timeMillis, continuation)

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext) =
        delay.invokeOnTimeout(timeMillis, block, context)
}