package io.github.homchom.recode.render

import net.minecraft.client.gui.GuiGraphics

// TODO: add more to interface as side chat refactor continues (e.g. scale and scroll)

@Suppress("FunctionName")
interface SideChatComponent {
    fun `recode$renderSide`(guiGraphics: GuiGraphics, tickDelta: Int, mouseX: Int, mouseY: Int)
}