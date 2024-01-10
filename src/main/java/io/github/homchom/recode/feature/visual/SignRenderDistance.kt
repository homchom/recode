package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.listenEach
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.render.RenderBlockEntitiesEvent
import net.minecraft.world.level.block.entity.SignBlockEntity

object FSignRenderDistance {
    init {
        registerFeature("Sign Render Distance") {
            onEnable { renderSigns() }
        }
    }

    private fun Power.renderSigns() {
        listenEach(RenderBlockEntitiesEvent) { context ->
            for (element in context) {
                val blockEntity = element.value
                if (blockEntity is SignBlockEntity) {
                    val cameraPos = mc.cameraEntity!!.blockPosition()
                    val distance = Config.getInteger("signRenderDistance").toDouble()
                    if (!blockEntity.getBlockPos().closerThan(cameraPos, distance)) {
                        element.invalidate()
                    }
                }
            }
        }
    }
}