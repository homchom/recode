@file:JvmName("Globals")

package io.github.homchom.recode

import io.github.homchom.recode.config.Config
import io.github.homchom.recode.feature.meta.FDebugMode
import net.minecraft.SharedConstants
import net.minecraft.client.Minecraft

val mc: Minecraft get() = Minecraft.getInstance()

// TODO: should this be built into a higher level extension function instead?
val currentDataVersion get() = SharedConstants.getCurrentVersion().dataVersion.version

// TODO: remove false positives during config rework
val debug: Boolean get() = Config[FDebugMode] ?: true