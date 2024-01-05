@file:JvmName("LocalPlayerFunctions")

package io.github.homchom.recode.multiplayer

/**
 * @param command The command to send, without the leading slash.
 *
 * @see DelayedCommandSender
 */
fun sendCommand(command: String) = DelayedCommandSender.sendCommand(command)