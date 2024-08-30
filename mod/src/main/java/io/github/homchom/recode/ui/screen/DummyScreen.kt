package io.github.homchom.recode.ui.screen

import io.github.homchom.recode.ui.text.toVanilla
import net.kyori.adventure.text.Component
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen

/**
 * A dummy [Screen] whose sole purpose is to release the mouse. This is, for example, useful in conjunction
 * with native IO functions like [io.github.homchom.recode.io.pickFile].
 */
class DummyScreen(
    title: Component,
    private val renderBackground: Boolean = true
) : Screen(title.toVanilla()) {
    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, tickDelta: Float) {
        if (renderBackground) renderBackground(guiGraphics, mouseX, mouseY, tickDelta)
    }
}