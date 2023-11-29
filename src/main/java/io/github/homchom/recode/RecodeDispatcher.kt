package io.github.homchom.recode

import io.github.homchom.recode.ui.sendSystemToast
import io.github.homchom.recode.ui.text.translateText
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
 * A [CoroutineDispatcher] and [CoroutineExceptionHandler] that confines execution to Minecraft's main
 * thread. Use this instead of Minecraft's default [Executor] because this, runs first and supports the
 * [kotlinx.coroutines.yield] function.
 */
object RecodeDispatcher : CoroutineContext {
    private val dispatcher = object : CoroutineDispatcher() {
        val pending = ConcurrentLinkedQueue<Runnable>()

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
    }

    private val delegate = dispatcher + CoroutineExceptionHandler { _, exception ->
        mc.sendSystemToast(
            translateText("recode.uncaught_exception.toast.title"),
            translateText("recode.uncaught_exception.toast")
        )
        runOnMinecraftThread {
            val thread = Thread.currentThread()
            thread.uncaughtExceptionHandler.uncaughtException(thread, exception)
        }
    }

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>) = delegate[key]
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R) =
        delegate.fold(initial, operation)
    override fun minusKey(key: CoroutineContext.Key<*>) = delegate.minusKey(key)

    /**
     * Tells the dispatcher to expedite previously dispatched blocks. If this is called from Minecraft's main
     * thread, it is guaranteed that all blocks will complete before the function returns.
     */
    fun expedite() = with(dispatcher.pending) {
        if (!mc.isSameThread) return
        while (!isEmpty()) remove().run()
    }
}