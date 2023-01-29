@file:JvmName("Blaze3DExtensions")

package io.github.homchom.recode.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexMultiConsumer
import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.RGBAColor
import net.minecraft.client.renderer.MultiBufferSource

inline fun runOnRenderThread(crossinline block: () -> Unit) {
    if (RenderSystem.isOnRenderThread()) {
        block()
    } else {
        RenderSystem.recordRenderCall { block() }
    }
}

fun MultiBufferSource.withOutline(color: RGBAColor) = MultiBufferSource { type ->
    val buffer = getBuffer(type)
    val outline = type.outline()
    if (outline.isPresent) {
        val outlineSource = mc.renderBuffers().outlineBufferSource()
        outlineSource.setColor(color.red, color.green, color.blue, color.alpha)
        val outlineBuffer = outlineSource.getBuffer(outline.get())
        VertexMultiConsumer.create(outlineBuffer, buffer)
    } else {
        buffer
    }
}