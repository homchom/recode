package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.listenEach
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.hypercube.state.PlotMode
import io.github.homchom.recode.hypercube.state.currentDFState
import io.github.homchom.recode.hypercube.state.isInMode
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.OutlineBlockEntitiesEvent
import io.github.homchom.recode.render.rgba
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

object FCodeSearch {
    init {
        registerFeature("Code Search") {
            onEnable { outlineBlockEntities() }
        }
    }

    private fun Power.outlineBlockEntities() {
        listenEach(OutlineBlockEntitiesEvent) { context ->
            if (!currentDFState.isInMode(PlotMode.Dev)) return@listenEach
            for (element in context) {
                val blockEntity = element.blockEntity
                if (blockEntity is SignBlockEntity && CodeSearcher.isSignMatch(blockEntity)) {
                    val distance = sqrt(blockEntity.getBlockPos().distSqr(mc.cameraEntity!!.blockPosition()))
                    // TODO: test if alpha actually makes a difference
                    val alpha = (distance.coerceIn(1.0, 15.0) * 17).toInt()
                    element.outlineColor = rgba(255, 255, 255, alpha)
                }
            }
        }
    }
}