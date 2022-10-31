package io.github.homchom.recode.feature.rendering

import com.mojang.blaze3d.vertex.BufferBuilder
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.OutlineBlockEntityEvent
import io.github.homchom.recode.render.RenderBlockEntityEvent
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.sys.player.DFInfo
import io.github.homchom.recode.ui.rgba
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.entity.SignBlockEntity
import kotlin.math.sqrt

val FCodeSearch = feature("Code Search") {
    onLoad {
        OutlineBlockEntityEvent.listen { blockEntity, result ->
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

        RenderBlockEntityEvent.listen { info, result ->
            if (info.block is SignBlockEntity
                && DFInfo.currentState.getMode() == LegacyState.Mode.DEV
                && mc.player!!.isCreative
                && CodeSearcher.isSignMatch(info.block)
            ) {
                info.poseStack.pushPose()

                try {
                    val builder = info.bufferSource.getBuffer(RenderType.lines()) as BufferBuilder

                    builder.vertex(
                        info.poseStack.last().pose(),
                        0.5f, 0.5f, 0.5f
                    )
                    builder.color(0f, 1f, 0f, 1f)
                    builder.normal(0f, 0f, 0f)
                    builder.endVertex()

                    val cameraPos = mc.gameRenderer.mainCamera.position
                        .subtract(
                            info.block.blockPos.x.toDouble(),
                            info.block.blockPos.y.toDouble(),
                            info.block.blockPos.z.toDouble()
                        )
                        .add(
                            mc.gameRenderer.mainCamera.lookVector.x().toDouble(),
                            mc.gameRenderer.mainCamera.lookVector.y().toDouble(),
                            mc.gameRenderer.mainCamera.lookVector.z().toDouble(),
                        )

                    builder.vertex(
                        info.poseStack.last().pose(),
                        cameraPos.x.toFloat(),
                        cameraPos.y.toFloat(),
                        cameraPos.z.toFloat()
                    )
                    builder.color(0f, 0f, 1f, 1f)
                    builder.normal(0f, 0f, 0f)
                    builder.endVertex()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                info.poseStack.popPose()
            }
            result
        }
    }
}