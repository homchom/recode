@file:JvmName("RenderEvents")

package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.Power
import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.mc
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.math.MixedInt
import io.github.homchom.recode.util.std.mapToArray
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.core.SectionPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult

val BeforeOutlineBlockEvent =  wrapFabricEvent(WorldRenderEvents.BEFORE_BLOCK_OUTLINE) { listener ->
    BeforeBlockOutline { worldRenderContext, hitResult ->
        RenderSystem.assertOnRenderThread()
        val context = BlockOutlineContext(worldRenderContext, hitResult)
        listener(context)
        context.isValid
    }
}

data class BlockOutlineContext(
    val worldRenderContext: WorldRenderContext,
    val hitResult: HitResult?,
    override var validity: MixedInt = MixedInt(1)
) : Validated

val RenderBlockEntitiesEvent = createEvent { list: List<SimpleValidated<BlockEntity>> -> list.mapValid() }

val OutlineBlockEntitiesEvent =
    createBufferedEvent<Array<out BlockEntityOutlineContext>, _, BlockEntityOutlineContext.Input, _>(
        resultCapture = { context ->
            context
                .filter { it.outlineColor != null }
                .associate { it.blockEntity.blockPos to it.outlineColor!! }
        },
        stableInterval = 3.ticks,
        keySelector = { Case(it.sectionPos) },
        contextGenerator = { input ->
            input.blockEntities.mapToArray(::BlockEntityOutlineContext)
        }
    ).also { event ->
        event.use(Power(
            onEnable = {
                listenEach(BeforeOutlineBlockEvent) { context ->
                    val processor = context.worldRenderContext.worldRenderer() as DRecodeLevelRenderer
                    processor.`recode$processOutlines`(mc.frameTime)
                }
            }
        ))
    }

data class BlockEntityOutlineContext @JvmOverloads constructor(
    val blockEntity: BlockEntity,
    var outlineColor: RGBAColor? = null
) {
    data class Input(val blockEntities: Collection<BlockEntity>, val sectionPos: SectionPos?)
}

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
     * Gets and returns the [RGBAColor] of [blockEntity]'s outline, or `null` if it will not be outlined.
     */
    fun `recode$getBlockEntityOutlineColor`(blockEntity: BlockEntity): RGBAColor?

    /**
     * Processes all unprocessed entity and block entity outlines.
     */
    fun `recode$processOutlines`(partialTick: Float)
}