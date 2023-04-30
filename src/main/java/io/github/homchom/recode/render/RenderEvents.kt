package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.event.*
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

object BeforeOutlineBlockEvent :
    WrappedEvent<BlockOutlineContext, BeforeBlockOutline> by
        wrapFabricEvent(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, { listener ->
            BeforeBlockOutline { worldRenderContext, hitResult ->
                RenderSystem.assertOnRenderThread()
                runBlocking {
                    val context = BlockOutlineContext(worldRenderContext, coroutineContext, hitResult)
                    listener(context)
                    context.isValid.get()
                }
            }
        })

data class BlockOutlineContext(
    val worldRenderContext: WorldRenderContext,
    val coroutineContext: CoroutineContext,
    val hitResult: HitResult?,
    override val isValid: AtomicBoolean = AtomicBoolean(true)
) : Validated

object RenderBlockEntityEvent :
    SimpleValidatedEvent<BlockEntity> by createValidatedEvent()