@file:JvmName("CommandQueue")

package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.mc
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// https://github.com/PaperMC/Velocity/issues/909
private val queue = ArrayDeque<String>()

@OptIn(DelicateCoroutinesApi::class)
fun unsafelySendCommand(command: String) {
    // https://github.com/PaperMC/Velocity/issues/909 TODO: remove queue when fixed
    queue += command
    if (queue.size == 1) GlobalScope.launch(RecodeDispatcher) {
        try {
            do {
                val next = queue.removeFirst()
                mc.player?.connection?.sendUnsignedCommand(next) ?: break
                delay(1.ticks)
            } while (queue.isNotEmpty())
        } finally {
            queue.clear()
        }
    }
}