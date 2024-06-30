@file:JvmName("Blaze3DExtensions")

package io.github.homchom.recode.render

import com.mojang.blaze3d.vertex.VertexMultiConsumer
import io.github.homchom.recode.mc
import net.minecraft.client.renderer.MultiBufferSource

@JvmName("withOutline")
fun MultiBufferSource.withOutline(color: RGBA) = MultiBufferSource { type ->
    val buffer = getBuffer(type)
    val outline = type.outline()
    if (outline.isPresent) {
        val outlineSource = mc.renderBuffers().outlineBufferSource()
        outlineSource.setColor(color.red(), color.green(), color.blue(), color.alpha())
        val outlineBuffer = outlineSource.getBuffer(outline.get())
        VertexMultiConsumer.create(outlineBuffer, buffer)
    } else {
        buffer
    }
}