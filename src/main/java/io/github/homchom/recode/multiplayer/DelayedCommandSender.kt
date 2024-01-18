package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.hypercube.state.DFStateDetectors
import io.github.homchom.recode.hypercube.state.PlotMode
import io.github.homchom.recode.hypercube.state.currentDFState
import io.github.homchom.recode.hypercube.state.isInMode
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.DelayedCommandSender.sendCommand
import io.github.homchom.recode.util.coroutines.RateLimiter
import io.github.homchom.recode.util.math.per
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration.Companion.seconds

/**
 * An object for safely sending commands with
 * [inversion of control](https://en.wikipedia.org/wiki/Inversion_of_control), delayed if needed to
 * prevent a kick for spamming and with protection against malicious plots sending stateful messages.
 *
 * @see sendCommand
 */
object DelayedCommandSender {
    private val limiter = RateLimiter(rate = 1 per 1.seconds)
    // TODO: make magic number 5 (50%) a config setting
    private val internalLimiter = RateLimiter(rate = 1 per 1.seconds, limit = 5)
    private var delayQueue: ArrayDeque<String>? = null

    private val limit by internalLimiter::limit

    // https://github.com/PaperMC/Velocity/issues/909
    private val distinctionQueue = ArrayDeque<String>()

    @OptIn(DelicateCoroutinesApi::class)
    fun sendCommand(command: String) {
        // first, protect against malicious plots
        if (!internalLimiter.record() && currentDFState.isInMode(PlotMode.Play)) {
            DFStateDetectors.panic()
        }

        // if delayQueue != null, we are on cooldown
        delayQueue?.let { queue ->
            queue += command
            return
        }

        // otherwise, try to send the command immediately
        if (limiter.count.value < limit) {
            sendCommandUnsafe(command)
            return
        }

        // resort to a cooldown by initializing delayQueue
        delayQueue = ArrayDeque(listOf(command))
        GlobalScope.launch(RecodeDispatcher) {
            try {
                processCooldownQueue()
            } catch (any: Throwable) {
                delayQueue = null
            }
        }
    }

    private suspend fun processCooldownQueue() {
        var prevCount = 0
        internalLimiter.count.collect { count ->
            if (count >= limit) return@collect
            if (count >= prevCount) {
                prevCount = count
                return@collect
            }
            prevCount = count

            val queue = delayQueue!!
            if (queue.isEmpty()) {
                this.delayQueue = null
                coroutineContext.cancel()
                return@collect
            }
            sendCommandUnsafe(queue.removeFirst())
        }
    }

    fun record() = limiter.record()

    @OptIn(DelicateCoroutinesApi::class)
    fun sendCommandUnsafe(command: String) {
        // https://github.com/PaperMC/Velocity/issues/909 TODO: remove distinctionQueue when fixed
        distinctionQueue += command
        if (distinctionQueue.size == 1) GlobalScope.launch(RecodeDispatcher) {
            try {
                do {
                    val next = distinctionQueue.removeFirst()
                    mc.player?.connection?.sendUnsignedCommand(next) ?: break
                    delay(1.ticks)
                } while (distinctionQueue.isNotEmpty())
            } finally {
                distinctionQueue.clear()
            }
        }
    }
}