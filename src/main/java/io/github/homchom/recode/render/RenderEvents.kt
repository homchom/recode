package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.event.*
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult
import kotlin.coroutines.CoroutineContext

object BeforeOutlineBlockEvent :
    WrappedHook<BlockOutlineContext, Boolean, BeforeBlockOutline> by
        wrapFabricEvent(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, { listener ->
            BeforeBlockOutline { worldRenderContext, hitResult ->
                RenderSystem.assertOnRenderThread()
                runBlocking {
                    val context = BlockOutlineContext(worldRenderContext, coroutineContext, hitResult)
                    listener(context, true)
                }
            }
        }),
    ValidatedHook<BlockOutlineContext>

data class BlockOutlineContext(
    val worldRenderContext: WorldRenderContext,
    val coroutineContext: CoroutineContext,
    val hitResult: HitResult?
)

object RenderBlockEntityEvent :
    CustomHook<BlockEntity, Boolean> by createHook(),
    ValidatedHook<BlockEntity>