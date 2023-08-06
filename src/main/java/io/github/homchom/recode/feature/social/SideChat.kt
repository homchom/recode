package io.github.homchom.recode.feature.social

import io.github.homchom.recode.mod.config.Config
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ChatComponent
import kotlin.math.min

// side chat doesn't use a FeatureModule because it doesn't need one yet TODO: reconcile after config refactor

class SideChat(private val mc: Minecraft) : ChatComponent(mc) {
    // TODO: why -2?
    val xOffset get() = mc.window.guiScaledWidth - width - 2

    constructor() : this(io.github.homchom.recode.mc)

    override fun render(guiGraphics: GuiGraphics, tickDelta: Int, mouseX: Int, mouseY: Int) {
        with(guiGraphics.pose()) {
            pushPose()
            translate(xOffset.toFloat(), 0f, 0f)
            try {
                super.render(guiGraphics, tickDelta, mouseX, mouseY)
            } finally {
                popPose()
            }
        }
    }

    override fun getWidth(): Int {
        val configWidth = Config.getInteger("sidechat_width")
        if (configWidth > 0) return configWidth

        // TODO: why -14?
        val rawWidth = min(mc.window.guiScaledWidth - super.getWidth() - 14, super.getWidth())
        return if (rawWidth > 0) rawWidth else 1
    }
}

@Suppress("FunctionName")
interface MCGuiWithSideChat {
    fun `recode$getSideChat`(): SideChat
}