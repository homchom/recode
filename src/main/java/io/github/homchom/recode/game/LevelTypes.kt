package io.github.homchom.recode.game

import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.ChunkPos

data class ChunkPos3D(val x: Int, val y: Int, val z: Int) {
    constructor(blockPos: BlockPos) : this(shift(blockPos.x), shift(blockPos.y), shift(blockPos.z))

    fun toChunkPos() = ChunkPos(x, z)
}

private fun shift(coord: Int) = SectionPos.blockToSectionCoord(coord)