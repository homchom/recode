package io.github.homchom.recode.ui

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style

val FORMATTING_CODE_REGEX = Regex("""§(?:[0-9a-fk-o]|x(?:§[0-9a-f]){6})""", RegexOption.IGNORE_CASE)

operator fun MutableComponent.plusAssign(component: Component) {
    append(component)
}

/**
 * Removes all § formatting codes from [componentString].
 */
fun unstyle(componentString: String) = componentString.replace(FORMATTING_CODE_REGEX, "")

val Component.unstyledString get() = unstyle(string)

infix fun Component.looksLike(other: Component) =
    toFlatList(Style.EMPTY) == other.toFlatList(Style.EMPTY)

fun Component.equalsUnstyled(string: String) = unstyledString == string

fun Regex.matchEntireUnstyled(text: Component) = matchEntire(text.unstyledString)

infix fun Regex.matchesUnstyled(text: Component) = matches(text.unstyledString)