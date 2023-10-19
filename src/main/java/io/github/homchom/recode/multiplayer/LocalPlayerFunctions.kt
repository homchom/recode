@file:JvmName("LocalPlayerFunctions")

package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.mc
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component

/**
 * @throws IllegalStateException if there is no current player
 */
fun displayMessage(message: Component) = asPlayer { displayClientMessage(message, false) }

/**
 * @param command The command to send, without the leading slash.
 *
 * @throws IllegalStateException if there is no current player
 */
fun sendCommand(command: String) = asPlayer { DelayedCommandSender.sendUnsigned(command, connection) }

private inline fun <R> asPlayer(block: LocalPlayer.() -> R) = mc.player?.block()
    ?: error("There is no current player")

// https://github.com/PaperMC/Velocity/issues/909 TODO: remove
private object DelayedCommandSender {
    private val queue = ArrayDeque<String>()

    @OptIn(DelicateCoroutinesApi::class)
    fun sendUnsigned(command: String, connection: ClientPacketListener) {
        queue += command
        if (queue.size == 1) GlobalScope.launch(RecodeDispatcher) {
            while (queue.isNotEmpty()) {
                connection.sendUnsignedCommand(queue.first())
                delay(1.ticks)
                queue.removeFirst()
            }
        }
    }
}