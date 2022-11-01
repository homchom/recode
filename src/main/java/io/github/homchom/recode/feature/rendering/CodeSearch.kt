package io.github.homchom.recode.feature.rendering

import com.mojang.blaze3d.vertex.BufferBuilder
import io.github.homchom.recode.feature.feature
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.config.Config
import io.github.homchom.recode.mod.features.commands.CodeSearcher
import io.github.homchom.recode.render.OutlineBlockEntityEvent
import io.github.homchom.recode.render.RenderBlockEntityEvent
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.sys.player.DFInfo
import io.github.homchom.recode.ui.rgba
import net.minecraft.client.renderer.RenderType
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.phys.Vec3
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
                && Config.getBoolean("codeSearchTracers")
            ) {
                info.poseStack.pushPose()

                try {
                    val builder = info.bufferSource.getBuffer(RenderType.lines()) as BufferBuilder

                    var pulseStart = 0f
                    var pulseEnd = 1f

                    if (Config.getBoolean("codeSearchPulse")) {
                        var pulseProgress = (((mc.level!!.gameTime % 40).toFloat() + mc.deltaFrameTime) % 40) / 20

                        if (pulseProgress > 1) {
                            pulseProgress = 2 - pulseProgress
                        }

                        pulseStart = pulseProgress * 0.8f
                        pulseEnd = pulseProgress * 0.8f + 0.2f
                    }

                    val startPos = Vec3(0.5, 0.5, 0.5)

                    val endPos = mc.gameRenderer.mainCamera.position
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

                    val lineStart = startPos.lerp(endPos, pulseStart.toDouble())
                    val lineEnd = startPos.lerp(endPos, pulseEnd.toDouble())

                    builder.vertex(
                        info.poseStack.last().pose(),
                        lineStart.x.toFloat(),
                        lineStart.y.toFloat(),
                        lineStart.z.toFloat()
                    )
                    builder.color(0f, 1f, 0f, 1f)
                    builder.normal(0f, 0f, 0f)
                    builder.endVertex()

                    builder.vertex(
                        info.poseStack.last().pose(),
                        lineEnd.x.toFloat(),
                        lineEnd.y.toFloat(),
                        lineEnd.z.toFloat()
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