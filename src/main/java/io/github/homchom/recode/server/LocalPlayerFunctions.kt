package io.github.homchom.recode.server

import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.literalText
import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.chat.Component

/**
 * @throws IllegalStateException if there is no current player
 */
fun sendMessage(message: Component) = asPlayer { displayClientMessage(message, false) }

/**
 * @throws IllegalStateException if there is no current player
 */
fun sendLiteralMessage(message: String) = sendMessage(literalText(message))

/**
 * @param command The command to send, without the leading slash.
 *
 * @throws IllegalStateException if there is no current player
 */
fun sendCommand(command: String) = asPlayer { connection.sendUnsignedCommand(command) }

private inline fun <R> asPlayer(block: LocalPlayer.() -> R) = mc.player?.block()
    ?: error("There is no current player to send a command as")