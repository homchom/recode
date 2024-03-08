package io.github.homchom.recode.render

import net.minecraft.core.SectionPos
import net.minecraft.world.level.block.entity.BlockEntity

/**
 * An [net.minecraft.client.renderer.LevelRenderer] that is augmented by recode.
 */
@Suppress("FunctionName")
interface DRecodeLevelRenderer {
    /**
     * @returns A filtered list of block entities that should still be rendered.
     */
    fun `recode$runBlockEntityEvents`(
        blockEntities: Collection<BlockEntity>,
        sectionPos: SectionPos?
    ): List<BlockEntity>

    /**
     * Gets and returns the RGBA hex of [blockEntity]'s outline, or `null` if it will not be outlined.
     */
    fun `recode$getBlockEntityOutlineColor`(blockEntity: BlockEntity): Int?

    /**
     * Processes all unprocessed entity and block entity outlines.
     */
    fun `recode$processOutlines`(partialTick: Float)
}