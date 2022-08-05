package io.github.homchom.recode.feature.rendering

import io.github.homchom.recode.event.RecodeEvents
import io.github.homchom.recode.feature.Feature
import io.github.homchom.recode.init.RModule
import io.github.homchom.recode.init.listenTo
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.config.Config
import net.minecraft.world.level.block.entity.SignBlockEntity

class FSignRenderDistance : Feature("Sign Render Distance") {
    override val dependencies = none()

    override fun RModule.onLoad() {
        listenTo(RecodeEvents.RenderBlockEntity) { blockEntity, render ->
            if (blockEntity is SignBlockEntity) {
                val cameraPos = mc.cameraEntity!!.blockPosition()
                val distance = Config.getInteger("signRenderDistance").toDouble()
                if (!blockEntity.getBlockPos().closerThan(cameraPos, distance)) {
                    return@listenTo false
                }
            }
            render
        }
    }

    override fun RModule.onEnable() = Unit
    override fun RModule.onDisable() = Unit
}