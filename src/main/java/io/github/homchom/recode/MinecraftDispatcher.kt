package io.github.homchom.recode

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

/**
 * Runs [block] on Minecraft's main thread, blocking the current thread until completion. Use this over
 * [runBlocking] because this function interoperates with [MinecraftDispatcher] to prevent deadlocks.
 */
@OptIn(ExperimentalStdlibApi::class)
fun <R> runOnMinecraftThread(block: suspend CoroutineScope.() -> R) =
    if (mc.isSameThread) {
        runBlocking {
            blockingEventLoop = coroutineContext[CoroutineDispatcher]
            try {
                block()
            } finally {
                blockingEventLoop = null
            }
        }
    } else {
        runBlocking(MinecraftDispatcher) { block() }
    }

private var blockingEventLoop: CoroutineDispatcher? = null

/**
 * A [CoroutineDispatcher] that confines execution to Minecraft's main thread.
 */
object MinecraftDispatcher : CoroutineDispatcher() {
    private val raw = mc.asCoroutineDispatcher()

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val delegate = blockingEventLoop ?: raw
        delegate.dispatch(context, block)
    }
}