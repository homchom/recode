package io.github.homchom.recode.ui

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

val FORMATTING_CODE_REGEX = Regex("§.")

operator fun MutableComponent.plusAssign(component: Component) {
    append(component)
}

val Component.unstyledString get() = string.replace(FORMATTING_CODE_REGEX, "")

infix fun Component.looksLike(other: Component) =
    toFlatList(Style.EMPTY) == other.toFlatList(Style.EMPTY)

fun Component.equalsUnstyled(string: String) = unstyledString == string

fun Regex.matchEntireUnstyled(text: Component) = matchEntire(text.unstyledString)

infix fun Regex.matchesUnstyled(text: Component) = matches(text.unstyledString)