package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.hypercube.state.DFStateDetectors
import io.github.homchom.recode.hypercube.state.PlotMode
import io.github.homchom.recode.hypercube.state.currentDFState
import io.github.homchom.recode.hypercube.state.isInMode
import io.github.homchom.recode.util.coroutines.RateLimiter
import io.github.homchom.recode.util.math.per
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

/**
 * Creates a [Sender] by [function] that cancels sending on invocations of [lifecycle].
 */
inline fun <T, R> Sender(lifecycle: Listenable<*>, crossinline function: suspend (T) -> R) =
    Sender { input: T ->
        coroutineScope {
            val lifecycleJob = lifecycle.notifications
                .onEach { cancel() }
                .launchIn(this)
            function(input).also { lifecycleJob.cancel() }
        }
    }

/**
 * An object that can [send] inputs in a suspending manner, cancelling if the action can no longer
 * be finished during sending.
 *
 * @see io.github.homchom.recode.event.Requester
 */
fun interface Sender<in T, out R> {
    suspend fun send(input: T): R

    /**
     * A companion object to [Sender] with functions for safe sending, i.e. of commands.
     */
    companion object {
        /**
         * Safely sends [command] with [inversion of control](https://en.wikipedia.org/wiki/Inversion_of_control),
         * delaying if needed to prevent a kick for spamming and with protection against malicious plots sending
         * stateful messages. This function suspends and should be cancelled by its containing
         * [io.github.homchom.recode.event.Requester] if the command would cause problems if sent after the delay.
         *
         * @param command The command to send, without the leading slash.
         */
        suspend fun sendCommand(command: String) = CommandSender.sendCommand(command)

        /**
         * Records that a command has been sent by the client, notifying [sendCommand]'s rate limiter.
         */
        fun recordCommand() = CommandSender.record()
    }
}

suspend fun <R> Sender<Unit, R>.send() = send(Unit)

private object CommandSender {
    private val limiter = RateLimiter(rate = 1 per 1.seconds)
    // TODO: make magic number 6 (60%) a config setting
    private val internalLimiter = RateLimiter(rate = 1 per 1.seconds, limit = 6)
    // TODO: should this use a channel?
    private var delayQueue: ArrayDeque<CompletableJob>? = null

    private val limit by internalLimiter::limit

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun sendCommand(command: String) {
        // first, protect against malicious plots
        if (!internalLimiter.record() && currentDFState.isInMode(PlotMode.Play)) {
            DFStateDetectors.panic()
        }

        // if limiter.count >= limit, we are on cooldown
        if (limiter.count.value >= limit) {
            if (delayQueue == null) {
                delayQueue = ArrayDeque()
                GlobalScope.launch(RecodeDispatcher) { processCooldownQueue() }
            }
            Job(coroutineContext[Job])
                .also { delayQueue?.add(it) }
                .join()
        }

        unsafelySendCommand(command)
    }

    private suspend fun processCooldownQueue() {
        try {
            var prevCount = 0
            internalLimiter.count.takeWhile t@{ count ->
                val prev = prevCount
                prevCount = count
                if (count >= limit) return@t true
                if (count >= prev) return@t true

                val queue = delayQueue!!
                queue.removeFirst().complete()
                queue.isNotEmpty()
            }.collect()
        } finally {
            delayQueue = null
        }
    }

    fun record() = limiter.record()
}