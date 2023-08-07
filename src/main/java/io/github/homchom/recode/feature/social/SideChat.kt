package io.github.homchom.recode.feature.social

import io.github.homchom.recode.mod.config.Config
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ChatComponent
import kotlin.math.min

// side chat doesn't use a FeatureModule because it doesn't need one yet TODO: reconcile after config refactor

private const val MIN_CHAT_PADDING = 16 // TODO: config setting?

class SideChat(private val mc: Minecraft) : ChatComponent(mc) {
    val xOffset get() = mc.window.guiScaledWidth - width - tailWidth(scale)

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

        // auto-fit if configWidth <= 0
        val actualParentWidth = super.getWidth() + tailWidth(super.getScale())
        val flexWidth = mc.window.guiScaledWidth - actualParentWidth - MIN_CHAT_PADDING
        val actualWidth = min(actualParentWidth, flexWidth).coerceAtLeast(1)

        // tail width is subtracted and subsequently re-added for superclass compatibility
        return actualWidth - tailWidth(scale)
    }

    // mc chat is rendered 12 * scale pixels wider than configured, on the right edge (deduced from superclass)
    private fun tailWidth(scale: Double) = (12 * scale).toInt()
}

@Suppress("FunctionName")
interface MCGuiWithSideChat {
    fun `recode$getSideChat`(): SideChat
}