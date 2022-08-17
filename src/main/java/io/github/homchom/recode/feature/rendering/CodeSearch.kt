package io.github.homchom.recode.feature.rendering

import io.github.homchom.recode.feature.Feature
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.OutlineBlockEntityEvent
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.sys.player.DFInfo
import io.github.homchom.recode.ui.rgba
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

object FCodeSearch : Feature by feature("Code Search", {
    onLoad {
        listenTo(OutlineBlockEntityEvent) { blockEntity, result ->
            if (blockEntity is SignBlockEntity) {
                if (DFInfo.currentState.getMode() == LegacyState.Mode.DEV && mc.player!!.isCreative) {
                    if (CodeSearcher.isSignMatch(blockEntity)) {
                        val distance = sqrt(
                            blockEntity.getBlockPos()
                                .distSqr(mc.cameraEntity!!.blockPosition())
                        )
                        // TODO: test if alpha actually makes a difference
                        val alpha = (distance.coerceIn(1.0, 15.0) * 17).toInt()
                        result.outlineColor = rgba(255, 255, 255, alpha)
                    }
                }
            }
            result
        }
    }
})