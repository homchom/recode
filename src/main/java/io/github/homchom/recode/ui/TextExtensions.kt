package io.github.homchom.recode.ui

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

val COLOR_CODE_REGEX = Regex("§.")

operator fun MutableComponent.plusAssign(component: Component) {
    append(component)
}

val Component.stringWithoutColor get() = string.replace(COLOR_CODE_REGEX, "")

infix fun Component.looksLike(other: Component) =
    toFlatList(Style.EMPTY) == other.toFlatList(Style.EMPTY)