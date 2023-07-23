@file:JvmName("Globals")

package io.github.homchom.recode

import io.github.homchom.recode.mod.config.Config
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft

val mc: Minecraft get() = Minecraft.getInstance()

// TODO: should this be built into a higher level extension function instead?
val currentDataVersion get() = SharedConstants.getCurrentVersion().dataVersion.version

val debug: Boolean get() = Config.getBoolean("debugMode")