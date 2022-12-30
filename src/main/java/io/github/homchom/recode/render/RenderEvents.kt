package io.github.homchom.recode.render

import io.github.homchom.recode.event.*
import io.github.homchom.recode.ui.RGBAColor
import io.github.homchom.recode.util.MutableCase
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.HitResult

object BeforeOutlineBlockEvent :
    WrappedHook<BlockOutlineContext, Boolean, BeforeBlockOutline> by
        wrapFabricEvent(WorldRenderEvents.BEFORE_BLOCK_OUTLINE, { listener ->
            BeforeBlockOutline { worldRenderContext, hitResult ->
                listener(BlockOutlineContext(worldRenderContext, hitResult), true)
            }
        }),
    ValidatedHook<BlockOutlineContext>

data class BlockOutlineContext(val worldRenderContext: WorldRenderContext, val hitResult: HitResult?)

object RenderBlockEntityEvent :
    CustomHook<BlockEntity, Boolean> by createHook(),
    ValidatedHook<BlockEntity>

object OutlineBlockEntityEvent :
    CustomHook<BlockEntity, MutableCase<RGBAColor>> by DependentHook(createHook(), CustomOutlineProcessor)