package io.github.homchom.recode.server

import io.github.homchom.recode.mc

/**
 * @param command The command to send, without the leading slash.
 *
 * @throws IllegalStateException if there is no current player
 */
fun sendCommand(command: String) = mc.player?.commandUnsigned(command)
    ?: error("There is no current player to send a command as")