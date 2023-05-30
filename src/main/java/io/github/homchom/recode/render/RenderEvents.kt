package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.ChunkPos3D
import io.github.homchom.recode.game.ticks
import io.github.homchom.recode.mc
import io.github.homchom.recode.util.AtomicMixedInt
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.collections.mapToArray
import io.github.homchom.recode.util.coroutines.MinecraftDispatcher
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
    BufferedCustomEvent<Array<out BlockEntityOutlineContext>, Map<BlockPos, RGBAColor>,
            BlockEntityOutlineContext.Input> by DependentBufferedEvent(
        createBufferedEvent(
            resultCapture = { context ->
                context
                    .filter { it.outlineColor.get() != null }
                    .associate { it.blockEntity.blockPos to it.outlineColor.get()!! }
            },
            interval = 3.ticks,
            keySelector = { Case(it.chunkPos) },
            contextGenerator = { input ->
                input.blockEntities.mapToArray { BlockEntityOutlineContext(it) }
            }
        ),
        {
            onEnable {
                BeforeOutlineBlockEvent.listenEach { context ->
                    val processor = context.worldRenderContext.worldRenderer() as OutlineProcessor
                    if (processor.needsOutlineProcessing()) {
                        withContext(MinecraftDispatcher) {
                            processor.processOutlines(mc.frameTime)
                        }
                    }
                }
            }
        }
    )

data class BlockEntityOutlineContext @JvmOverloads constructor(
    val blockEntity: BlockEntity,
    var outlineColor: AtomicReference<RGBAColor?> = AtomicReference(null)
) {
    data class Input(val blockEntities: Collection<BlockEntity>, val chunkPos: ChunkPos3D?)
}

interface OutlineProcessor {
    fun needsOutlineProcessing(): Boolean
    fun processOutlines(partialTick: Float)
    fun getBlockEntityOutlineColor(blockEntity: BlockEntity): RGBAColor?
}