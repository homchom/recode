package io.github.homchom.recode.feature.rendering

import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.OutlineBlockEntityEvent
import io.github.homchom.recode.server.PlotMode
import io.github.homchom.recode.server.currentDFState
import io.github.homchom.recode.server.isInMode
import io.github.homchom.recode.ui.rgba
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

val FCodeSearch = feature("Code Search") {
    onLoad {
        OutlineBlockEntityEvent.listenEach { context ->
            val blockEntity = context.blockEntity
            if (blockEntity is SignBlockEntity) {
                if (currentDFState.isInMode(PlotMode.Dev) && mc.player!!.isCreative) {
                    if (CodeSearcher.isSignMatch(blockEntity)) {
                        val distance = sqrt(
                            blockEntity.getBlockPos()
                                .distSqr(mc.cameraEntity!!.blockPosition())
                        )
                        // TODO: test if alpha actually makes a difference
                        val alpha = (distance.coerceIn(1.0, 15.0) * 17).toInt()
                        context.outlineColor = rgba(255, 255, 255, alpha)
                    }
                }
            }
        }
    }
}