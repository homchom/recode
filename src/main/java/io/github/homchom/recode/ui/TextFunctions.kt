package io.github.homchom.recode.ui

import io.github.homchom.recode.util.Matchable
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
fun Matchable<Component>.equalsUnstyled(string: String) = value.equalsUnstyled(string)

fun Regex.matchEntireUnstyled(text: Component) = matchEntire(text.unstyledString)
fun Regex.matchEntireUnstyled(text: Matchable<Component>) = matchEntireUnstyled(text.value)

infix fun Regex.matchesUnstyled(text: Component) = matches(text.unstyledString)
infix fun Regex.matchesUnstyled(text: Matchable<Component>) = matchesUnstyled(text.value)