package io.github.homchom.recode.render

import io.github.homchom.recode.event.*
import io.github.homchom.recode.ui.RGBAColor
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
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

object RenderBlockEntityEvent :
    CustomEvent<BlockEntity, Boolean> by createEvent(),
    ValidatedEvent<BlockEntity>

object OutlineBlockEntityEvent :
    CustomEvent<BlockEntity, OutlineResult> by DependentEvent(createEvent(), CustomOutlineProcessor)

class OutlineResult {
    var outlineColor: RGBAColor? = null
}