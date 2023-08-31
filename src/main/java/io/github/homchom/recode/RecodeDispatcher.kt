package io.github.homchom.recode

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * Runs [block] on Minecraft's main thread. If this is called from another thread, the calling thread is blocked
 * until completion.
 */
inline fun <R> runOnMinecraftThread(crossinline block: () -> R) =
    if (mc.isSameThread) block() else runBlocking(RecodeDispatcher) { block() }

/**
 * A [CoroutineDispatcher] that confines execution to Minecraft's main thread. Use this instead of Minecraft's
 * default [Executor] because this runs first and supports the [kotlinx.coroutines.yield] function.
 */
object RecodeDispatcher : CoroutineDispatcher() {
    private val pending = ConcurrentLinkedQueue<Runnable>()

    private val executor = Executor(pending::add)

    private val immediateExecutor = Executor { block ->
        if (mc.isSameThread) block.run() else executor.execute(block)
    }

    private val delegate = executor.asCoroutineDispatcher()
    private val immediateDelegate = immediateExecutor.asCoroutineDispatcher()

    override fun dispatch(context: CoroutineContext, block: Runnable) =
        immediateDelegate.dispatch(context, block)

    @InternalCoroutinesApi
    override fun dispatchYield(context: CoroutineContext, block: Runnable) =
        delegate.dispatchYield(context, block)

    /**
     * Tells the dispatcher to expedite previously dispatched blocks. If this is called from Minecraft's main
     * thread, it is guaranteed that all blocks will complete before the function returns.
     */
    fun expedite() {
        if (mc.isSameThread) {
            while (!pending.isEmpty()) pending.remove().run()
        }
    }
}