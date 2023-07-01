@file:JvmName("Toasts")

package io.github.homchom.recode.ui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastIds
import net.minecraft.network.chat.Component

@JvmOverloads
fun Minecraft.sendSystemToast(
    title: Component,
    body: Component,
    type: SystemToastIds = SystemToastIds.PERIODIC_NOTIFICATION
) {
    toasts.addToast(SystemToast.multiline(this, type, title, body))
}