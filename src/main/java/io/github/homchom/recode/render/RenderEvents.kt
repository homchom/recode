package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.ChunkPos3D
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.mc
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.MixedInt
import io.github.homchom.recode.util.collections.mapToArray
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult

object BeforeOutlineBlockEvent :
    WrappedEvent<BlockOutlineContext, BeforeBlockOutline> by
        wrapFabricEvent(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, { listener ->
            BeforeBlockOutline { worldRenderContext, hitResult ->
                RenderSystem.assertOnRenderThread()
                val context = BlockOutlineContext(worldRenderContext, hitResult)
                listener(context)
                context.isValid
            }
        })

data class BlockOutlineContext(
    val worldRenderContext: WorldRenderContext,
    val hitResult: HitResult?,
    override var validity: MixedInt = MixedInt(1)
) : Validated

object RenderBlockEntitiesEvent :
    SimpleValidatedListEvent<BlockEntity> by createEvent({ it.mapValid() })

object OutlineBlockEntitiesEvent :
    BufferedCustomEvent<
            Array<out BlockEntityOutlineContext>, Map<BlockPos, RGBAColor>, BlockEntityOutlineContext.Input
    > by createBufferedEvent(
        resultCapture = { context ->
            context
                .filter { it.outlineColor != null }
                .associate { it.blockEntity.blockPos to it.outlineColor!! }
        },
        stableInterval = 3.ticks,
        keySelector = { Case(it.chunkPos) },
        contextGenerator = { input ->
            input.blockEntities.mapToArray { BlockEntityOutlineContext(it) }
        }
    )
{
    init {
        dependency.depend(module { module ->
            module.extend(OutlineBlockEntitiesEvent.dependency)

            module.onEnable {
                BeforeOutlineBlockEvent.listenEach { context ->
                    val processor = context.worldRenderContext.worldRenderer() as RecodeLevelRenderer
                    processor.`recode$processOutlines`(mc.frameTime)
                }
            }
            module
        })
    }
}

data class BlockEntityOutlineContext @JvmOverloads constructor(
    val blockEntity: BlockEntity,
    var outlineColor: RGBAColor? = null
) {
    data class Input(val blockEntities: Collection<BlockEntity>, val chunkPos: ChunkPos3D?)
}

/**
 * An [net.minecraft.client.renderer.LevelRenderer] that is augmented by recode.
 */
@Suppress("FunctionName")
interface RecodeLevelRenderer {
    /**
     * @returns A filtered list of block entities that should still be rendered.
     */
    fun `recode$runBlockEntityEvents`(
        blockEntities: Collection<BlockEntity>,
        chunkPos: ChunkPos3D?
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