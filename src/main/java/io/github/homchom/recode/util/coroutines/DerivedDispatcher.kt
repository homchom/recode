package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.*
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

inline fun DerivedDispatcher(executor: Executor, crossinline immediatePredicate: () -> Boolean) =
    DerivedDispatcher(
        { command ->
            if (!immediatePredicate()) executor.execute(command)
        },
        executor
    )

fun DerivedDispatcher(executor: Executor) = DerivedDispatcher(executor) { false }

@OptIn(InternalCoroutinesApi::class)
class DerivedDispatcher private constructor(
    private val defaultDelegate: CoroutineDispatcher,
    private val yieldingDelegate: CoroutineDispatcher
) : CoroutineDispatcher(), Delay {
    private val delay get() = defaultDelegate as Delay

    constructor(defaultExecutor: Executor, yieldingExecutor: Executor) : this(
        defaultExecutor.asCoroutineDispatcher(),
        yieldingExecutor.asCoroutineDispatcher()
    )

    constructor(dispatcher: DerivedDispatcher) : this(
        dispatcher.defaultDelegate,
        dispatcher.yieldingDelegate
    )

    override fun dispatch(context: CoroutineContext, block: Runnable) =
        defaultDelegate.dispatch(context, block)

    override fun dispatchYield(context: CoroutineContext, block: Runnable) =
        yieldingDelegate.dispatchYield(context, block)

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) =
        delay.scheduleResumeAfterDelay(timeMillis, continuation)

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext) =
        delay.invokeOnTimeout(timeMillis, block, context)
}