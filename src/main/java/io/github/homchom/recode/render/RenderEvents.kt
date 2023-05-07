package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.ChunkPos3D
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.mc
import io.github.homchom.recode.util.AtomicMixedInt
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.collections.mapToArray
import kotlinx.coroutines.withContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult
import java.util.concurrent.atomic.AtomicReference

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
    override val validity: AtomicMixedInt = AtomicMixedInt(1)
) : Validated

object RenderBlockEntitiesEvent :
    SimpleValidatedListEvent<BlockEntity> by createEvent({ it.mapValid() })

object OutlineBlockEntitiesEvent :
    CustomEvent<BlockEntityOutlineContext, Map<BlockPos, RGBAColor>> by DependentEvent(
        createBufferedEvent(
            resultCapture = { context ->
                context.array
                    .filter { it.outlineColor.get() != null }
                    .associate { it.blockEntity.blockPos to it.outlineColor.get()!! }
            },
            interval = 2.ticks,
            keySelector = { Case(it.chunkPos) }
        ),
        {
            onEnable {
                BeforeOutlineBlockEvent.listenEach { context ->
                    val processor = context.worldRenderContext.worldRenderer() as OutlineProcessor
                    if (processor.needsOutlineProcessing()) {
                        withContext(RenderThreadContext) {
                            processor.processOutlines(mc.frameTime)
                        }
                    }
                }
            }
        }
    )

class BlockEntityOutlineContext(
    val array: Array<out Element>,
    val chunkPos: ChunkPos3D?
) {
    constructor(blockEntities: Collection<BlockEntity>, chunkPos: ChunkPos3D?) :
            this(blockEntities.mapToArray { Element(it) }, chunkPos)

    data class Element @JvmOverloads constructor(
        val blockEntity: BlockEntity,
        var outlineColor: AtomicReference<RGBAColor?> = AtomicReference(null)
    )
}

interface OutlineProcessor {
    fun needsOutlineProcessing(): Boolean
    fun processOutlines(partialTick: Float)
    fun getBlockEntityOutlineColor(blockEntity: BlockEntity): RGBAColor?
}