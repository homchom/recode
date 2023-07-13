package io.github.homchom.recode.render

import net.minecraft.client.gui.GuiGraphics

// TODO: add more to interface as side chat refactor continues (e.g. scale and scroll)

interface SideChatComponent {
    fun renderSide(guiGraphics: GuiGraphics, tickDelta: Int, mouseX: Int, mouseY: Int)
}