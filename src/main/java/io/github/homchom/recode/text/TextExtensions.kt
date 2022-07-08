package io.github.homchom.recode.text

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

operator fun MutableComponent.plusAssign(component: Component) {
    append(component)
}

operator fun Style.plus(style: Style): Style = applyTo(style)
operator fun Style.plus(format: ChatFormatting): Style = applyFormat(format)