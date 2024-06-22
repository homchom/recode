package io.github.homchom.recode

import io.github.homchom.recode.ui.sendSystemToast
import io.github.homchom.recode.ui.text.translatedText
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executor
import kotlin.coroutines.CoroutineContext

/**
 * Runs [block] on Minecraft's main thread. If this is called from another thread, the calling thread is
 * blocked until completion.
 */
inline fun <R> runOnMinecraftThread(crossinline block: () -> R) =
    if (mc.isSameThread) block() else runBlocking(RecodeDispatcher) { block() }

/**
 * A [kotlinx.coroutines.CoroutineDispatcher] and [CoroutineExceptionHandler] that confines execution to
 * Minecraft's main thread. This will always dispatch before Minecraft's default executor, and it supports
 * the [kotlinx.coroutines.yield] function.
 */
object RecodeDispatcher : CoroutineContext {
    private val pending = ConcurrentLinkedQueue<Runnable>()
    private val executor = Executor { task ->
        if (mc.isSameThread && mc.pendingTasksCount == 0) {
            task.run()
        } else synchronized(pending) {
            pending.add(task)
        }
    }

    private val delegate = executor.asCoroutineDispatcher() + RecodeExceptionHandler

    override fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>) = delegate[key]
    override fun <R> fold(initial: R, operation: (R, CoroutineContext.Element) -> R) =
        delegate.fold(initial, operation)
    override fun minusKey(key: CoroutineContext.Key<*>) = delegate.minusKey(key)

    /**
     * Tells the dispatcher to expedite previously dispatched blocks. If this is called from Minecraft's main
     * thread, it is guaranteed that all blocks will complete before the function returns.
     */
    fun expedite() {
        if (!mc.isSameThread) return
        val tasks = generateSequence(pending::poll).toList()
        for (task in tasks) task.run()
    }
}

/**
 * The [CoroutineExceptionHandler] used by [RecodeDispatcher].
 */
val RecodeExceptionHandler = CoroutineExceptionHandler { _, exception ->
    mc.sendSystemToast(
        translatedText("recode.uncaught_exception.toast.title"),
        translatedText("recode.uncaught_exception.toast")
    )
    val thread = Thread.currentThread()
    thread.uncaughtExceptionHandler.uncaughtException(thread, exception)
}