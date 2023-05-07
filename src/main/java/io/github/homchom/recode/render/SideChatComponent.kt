package io.github.homchom.recode.render

import com.mojang.blaze3d.vertex.PoseStack

// TODO: add more to interface as side chat refactor continues (e.g. scale and scroll)

interface SideChatComponent {
    fun renderSide(poseStack: PoseStack, tickDelta: Int, mouseX: Int, mouseY: Int)
}