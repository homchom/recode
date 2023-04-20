package io.github.homchom.recode.render

import com.mojang.blaze3d.vertex.PoseStack
import io.github.homchom.recode.event.*
import io.github.homchom.recode.ui.RGBAColor
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult

object BeforeOutlineBlockEvent :
    InvokableEvent<BlockOutlineContext, Boolean, BeforeBlockOutline> by
        wrapEvent(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, { listener ->
            BeforeBlockOutline { worldRenderContext, hitResult ->
                listener(BlockOutlineContext(worldRenderContext, hitResult), true)
            }
        }),
    ValidatedEvent<BlockOutlineContext>

data class BlockOutlineContext(val worldRenderContext: WorldRenderContext, val hitResult: HitResult?)

data class RenderBlockInfo(val block: BlockEntity, val poseStack: PoseStack, val bufferSource: MultiBufferSource)

object RenderBlockEntityEvent :
    CustomEvent<RenderBlockInfo, Boolean> by createEvent(),
    ValidatedEvent<RenderBlockInfo>

object OutlineBlockEntityEvent :
    CustomEvent<BlockEntity, OutlineResult> by DependentEvent(createEvent(), CustomOutlineProcessor)

class OutlineResult {
    var outlineColor: RGBAColor? = null
}