@file:JvmName("Globals")

package io.github.homchom.recode

import kotlinx.coroutines.asCoroutineDispatcher
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft

val mc: Minecraft get() = Minecraft.getInstance()

/**
 * A [kotlinx.coroutines.CoroutineDispatcher] that dispatches to Minecraft's main thread.
 */
val MinecraftDispatcher = mc.asCoroutineDispatcher()

// TODO: should this be built into a higher level extension function instead?
val currentDataVersion get() = SharedConstants.getCurrentVersion().dataVersion.version