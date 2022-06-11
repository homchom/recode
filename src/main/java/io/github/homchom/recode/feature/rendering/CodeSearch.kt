package io.github.homchom.recode.feature.rendering

import io.github.homchom.recode.event.RecodeEvents
import io.github.homchom.recode.feature.Feature
import io.github.homchom.recode.init.RModule
import io.github.homchom.recode.init.listenTo
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.CustomOutlineProcessor
import io.github.homchom.recode.render.GlobalUsesCustomOutlines
import io.github.homchom.recode.render.RGBA
import io.github.homchom.recode.sys.networking.DFState
import io.github.homchom.recode.sys.player.DFInfo
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

@OptIn(GlobalUsesCustomOutlines::class)
class FCodeSearch : Feature("Code Search") {
    override val dependencies = listOf(
        CustomOutlineProcessor()
    )

    override fun RModule.onLoad() {
        listenTo(RecodeEvents.OUTLINE_BLOCK_ENTITY) { blockEntity ->
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

    override fun RModule.onEnable() = Unit
    override fun RModule.onDisable() = Unit
}