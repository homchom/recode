package io.github.homchom.recode.feature.rendering

import io.github.homchom.recode.event.RecodeEvents
import io.github.homchom.recode.feature.Feature
import io.github.homchom.recode.init.RModule
import io.github.homchom.recode.init.addToEvent
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.RGBA
import io.github.homchom.recode.sys.networking.DFState
import io.github.homchom.recode.sys.player.DFInfo
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

class FCodeSearch : Feature("Code Search") {
    override val dependencies = none()

    override fun RModule.onLoad() = Unit

    override fun RModule.onEnable() {
        addToEvent(RecodeEvents.OUTLINE_BLOCK_ENTITY) { blockEntity: BlockEntity ->
            if (blockEntity is SignBlockEntity) {
                if (DFInfo.currentState.getMode() == DFState.Mode.DEV && mc.player!!.isCreative) {
                    if (CodeSearcher.isSignMatch(blockEntity)) {
                        val distance = sqrt(blockEntity.getBlockPos()
                            .distSqr(mc.cameraEntity!!.blockPosition()))
                        val alpha = (distance.coerceIn(1.0, 15.0) * 17).toInt()
                        outlineColor = RGBA(255, 255, 255, alpha)
                    }
                }
            }
        }
    }

    override fun RModule.onDisable() = Unit
}