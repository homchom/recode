package io.github.homchom.recode.game

import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import kotlin.time.Duration.Companion.milliseconds

val BlockPos.chunk get() = ChunkPos(this)

/**
 * Returns this integer as a [kotlin.time.Duration] in ticks, where 20 ticks = 1 second.
 */
val Int.ticks get() = milliseconds * 50