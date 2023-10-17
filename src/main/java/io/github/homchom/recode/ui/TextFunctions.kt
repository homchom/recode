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
    group(RegexModifier.IgnoreCase) {
        any("0-9a-fk-o")
        or
        str("x")
        group {
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
fun removeLegacyCodes(componentString: String) = componentString.replace(FORMATTING_CODE_REGEX, "")

val Component.unstyledString get() = removeLegacyCodes(string)

infix fun Component.looksLike(other: Component) =
    toFlatList(Style.EMPTY) == other.toFlatList(Style.EMPTY)

infix fun FormattedCharSequence.looksLike(other: FormattedCharSequence): Boolean {
    val list = mutableListOf<Any>() // even indices are styles; odd indices are code points
    accept { _, style, codePoint ->
        list += style
        list += codePoint
        true
    }
    var index = 0
    val result = other.accept { _, style, codePoint ->
        if (index == list.size) return@accept false
        style == list[index++] && codePoint == list[index++]
    }
    return result && index == list.size
}

fun Component.equalsUnstyled(string: String) = unstyledString == string

fun Regex.matchEntireUnstyled(text: Component) = matchEntire(text.unstyledString)

fun Regex.matchesUnstyled(text: Component) = matches(text.unstyledString)