@file:JvmName("TextFunctions")

package io.github.homchom.recode.ui

import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.regex
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence

val FORMATTING_CODE_REGEX = regex {
    str("§")
    all(RegexModifier.IgnoreCase) {
        any("0-9a-fk-o")
        or
        str("x")
        all {
            str("§")
            any("0-9a-f")
        } * 6
    }
}

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

infix fun FormattedCharSequence.looksLike(other: FormattedCharSequence): Boolean {
    val list = mutableListOf<Pair<Style, Int>>()
    accept { _, style, codePoint ->
        list += style to codePoint
        true
    }
    var index = 0
    return other.accept { _, otherStyle, otherCodePoint ->
        if (index == list.size) return@accept false
        val (style, codePoint) = list[index++]
        style == otherStyle && codePoint == otherCodePoint
    }
}

fun Component.equalsUnstyled(string: String) = unstyledString == string

fun Regex.matchEntireUnstyled(text: Component) = matchEntire(text.unstyledString)

infix fun Regex.matchesUnstyled(text: Component) = matches(text.unstyledString)