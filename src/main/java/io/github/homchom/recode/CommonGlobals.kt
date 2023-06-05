@file:JvmName("Globals")

package io.github.homchom.recode

import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft

val mc: Minecraft get() = Minecraft.getInstance()

// TODO: should this be built into a higher level extension function instead?
val currentDataVersion get() = SharedConstants.getCurrentVersion().dataVersion.version