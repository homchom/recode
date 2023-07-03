package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.feature.featureModule
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.render.RenderBlockEntitiesEvent
import net.minecraft.world.level.block.entity.SignBlockEntity

val FSignRenderDistance = featureModule("Sign Render Distance") {
    onEnable {
        RenderBlockEntitiesEvent.listenEach { context ->
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