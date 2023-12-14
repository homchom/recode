@file:JvmName("Toasts")

package io.github.homchom.recode.ui

import io.github.homchom.recode.ui.text.toVanilla
import net.kyori.adventure.text.Component
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds

/**
 * Sends a system toast notification of [type] with [title] and [body].
 */
@JvmOverloads
fun Minecraft.sendSystemToast(
    title: Component,
    body: Component,
    type: SystemToastIds = SystemToastIds.PERIODIC_NOTIFICATION
) {
    toasts.addToast(SystemToast.multiline(this, type, title.toVanilla(), body.toVanilla()))
}