@file:JvmName("RenderEvents")

package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import io.github.homchom.recode.Power
import io.github.homchom.recode.event.*
import io.github.homchom.recode.mc
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.math.MixedInt
import io.github.homchom.recode.util.std.mapToArray
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeBlockOutline
import net.minecraft.client.gui.GuiGraphics
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
        stableTickInterval = 3,
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

data class BlockEntityOutlineContext(
    val blockEntity: BlockEntity,
    var outlineColor: RGBA? = null
) {
    data class Input(val blockEntities: Collection<BlockEntity>, val sectionPos: SectionPos?)
}

val AfterRenderHudEvent = wrapFabricEvent(HudRenderCallback.EVENT) { listener ->
    HudRenderCallback { guiGraphics, tickDelta -> listener(HudRenderContext(guiGraphics, tickDelta)) }
}

data class HudRenderContext(val guiGraphics: GuiGraphics, val tickDelta: Float)