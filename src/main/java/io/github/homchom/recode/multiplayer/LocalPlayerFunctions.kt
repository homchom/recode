@file:JvmName("LocalPlayerFunctions")

package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.mc
import net.kyori.adventure.text.Component
import net.minecraft.client.player.LocalPlayer

/**
 * @throws IllegalStateException if there is no current player
 */
fun displayMessage(message: Component) = asPlayer { sendMessage(message) }

/**
 * @param command The command to send, without the leading slash.
 *
 * @see DelayedCommandSender
 */
fun sendCommand(command: String) = DelayedCommandSender.sendCommand(command)

private inline fun <R> asPlayer(block: LocalPlayer.() -> R) = mc.player?.block()
    ?: error("There is no current player")