package io.github.homchom.recode.feature.rendering

import io.github.homchom.recode.event.RecodeEvents
import io.github.homchom.recode.feature.Feature
import io.github.homchom.recode.init.RModule
import io.github.homchom.recode.init.listenTo
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.CustomOutlineProcessor
import io.github.homchom.recode.render.GlobalUsesCustomOutlines
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.sys.player.DFInfo
import io.github.homchom.recode.ui.rgba
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

@OptIn(GlobalUsesCustomOutlines::class)
class FCodeSearch : Feature("Code Search") {
    override val dependencies = listOf(
        CustomOutlineProcessor()
    )

    override fun RModule.onLoad() {
        listenTo(RecodeEvents.OutlineBlockEntity) { blockEntity, result ->
            if (blockEntity is SignBlockEntity) {
                if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV && mc.player!!.isCreative) {
                    if (CodeSearcher.isSignMatch(blockEntity)) {
                        val distance = sqrt(blockEntity.getBlockPos()
                            .distSqr(mc.cameraEntity!!.blockPosition()))
                        val alpha = (distance.coerceIn(1.0, 15.0) * 17).toInt()
                        result.outlineColor = rgba(255, 255, 255, alpha)
                    }
                }
            }
            result
        }
    }

    override fun RModule.onEnable() = Unit
    override fun RModule.onDisable() = Unit
}