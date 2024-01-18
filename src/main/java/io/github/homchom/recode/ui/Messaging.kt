@file:JvmName("Messaging")

package io.github.homchom.recode.ui

import io.github.homchom.recode.MOD_LOGO_CHAR
import io.github.homchom.recode.hypercube.MAIN_ARROW
import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.text.style
import io.github.homchom.recode.ui.text.text
import io.github.homchom.recode.ui.text.toVanilla
import net.kyori.adventure.text.Component
import net.minecraft.client.GuiMessageTag
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast

/**
 * Shows the current player a message, with a [net.minecraft.client.GuiMessageTag] of [tag].
 *
 * @see showRecodeMessage
 */
fun showMessage(message: Component, tag: GuiMessageTag = GuiMessageTag.system()) =
    mc.gui.chat.addMessage(message.toVanilla(), null, tag)

/**
 * Shows the current player a styled "recode message", including a prefixed logo.
 * Use for frontend response messages (such as command successes).
 *
 * @see RecodeMessageTags
 */
fun showRecodeMessage(message: Component, tag: GuiMessageTag = RecodeMessageTags.info) {
    val prefixed = text {
        literal("$MOD_LOGO_CHAR ")
        literal("$MAIN_ARROW ", style().color(tag.indicatorColor).bold())
        append(message)
    }
    showMessage(prefixed, tag)
}

/**
 * Sends a system toast notification of [type] with [title] and [body]. For more general messages,
 * consider using [showMessage].
 */
@JvmOverloads
fun Minecraft.sendSystemToast(
    title: Component,
    body: Component,
    type: SystemToast.SystemToastIds = SystemToast.SystemToastIds.PERIODIC_NOTIFICATION
) {
    toasts.addToast(SystemToast.multiline(this, type, title.toVanilla(), body.toVanilla()))
}