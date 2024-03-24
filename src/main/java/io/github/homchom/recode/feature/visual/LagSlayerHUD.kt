package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.Power
import io.github.homchom.recode.config.Config
import io.github.homchom.recode.config.Setting
import io.github.homchom.recode.event.listenEach
import io.github.homchom.recode.feature.AddsFeature
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.game.TickCountdown
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.ReceiveMessageEvent
import io.github.homchom.recode.render.AfterRenderHudEvent
import io.github.homchom.recode.ui.text.StyledString
import io.github.homchom.recode.ui.text.matchesPlain
import io.github.homchom.recode.ui.text.substring
import io.github.homchom.recode.ui.text.toVanillaComponent
import io.github.homchom.recode.util.regex.regex
import net.minecraft.client.gui.GuiGraphics

@OptIn(AddsFeature::class)
object FLagSlayerHUD : Setting<Boolean> {
    override val configString = "cpuOnScreen"

    private var regex: Regex? = null

    private var line1: StyledString? = null
    private var line2: StyledString? = null
    private var line3: StyledString? = null
    private val renderCountdown = TickCountdown(30)

    init {
        registerFeature("LagSlayer HUD") {
            onEnable {
                regex = regex {
                    str("CPU Usage: [")
                    str('▮') * 20
                    str("] (")
                    digit; digit.optional()
                    period; digit.oneOrMore()
                    str("%)")
                }

                interceptActionBarPackets()
                render()
            }
            onDisable {
                regex = null
            }
        }
    }

    private fun Power.interceptActionBarPackets() = listenEach(ReceiveMessageEvent.ActionBar) l@{ message ->
        if (Config[FLagSlayerHUD] == false) return@l
        if (!regex!!.matchesPlain(message.value)) return@l

        message.invalidate()

        val text = StyledString(message.value)
        line1 = text.substring(0, 11)
        line2 = text.substring(11, 33)
        line3 = text.substring(33)
        renderCountdown.wind()
    }

    private fun Power.render() = listenEach(AfterRenderHudEvent) l@{ (guiGraphics) ->
        if (!renderCountdown.isActive) return@l
        drawString(guiGraphics, line1!!, 3)
        drawString(guiGraphics, line2!!, 2)
        drawString(guiGraphics, line3!!, 1)
    }

    // TODO: rework post-workflow
    private fun drawString(guiGraphics: GuiGraphics, string: StyledString, index: Int) {
        val font = mc.font
        val window = mc.window
        guiGraphics.drawString(
            font,
            string.toVanillaComponent(),
            4,
            window.guiScaledHeight - (font.lineHeight * index + 4),
            0xffffff,
            true
        )
    }
}