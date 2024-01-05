@file:JvmName("MessageTags")

package io.github.homchom.recode.ui

import io.github.homchom.recode.MOD_NAME
import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.render.IntegralColor
import io.github.homchom.recode.ui.text.*
import net.kyori.adventure.text.ComponentLike
import net.minecraft.client.GuiMessageTag

object RecodeMessageTags {
    val info = tag(ColorPalette.GREEN, "info")

    val alert = tag(ColorPalette.LIGHT_PURPLE, "alert")

    val error = tag(ColorPalette.GOLD, "error")

    fun stacked(amount: Int) = tag(
        ColorPalette.AQUA,
        "stacked x$amount",
        "stacked",
        arrayOf(literalText(amount))
    )

    private fun tag(
        color: IntegralColor,
        string: String,
        translationKey: String = string,
        translationArgs: Array<out ComponentLike> = emptyArray()
    ): GuiMessageTag {
        return GuiMessageTag(
            color.toInt(),
            GuiMessageTag.Icon.CHAT_MODIFIED,
            translatedText(
                "chat.tag.recode.$translationKey",
                style().color(color),
                translationArgs
            ).toVanilla(),
            "$MOD_NAME $string"
        )
    }
}

/**
 * Combines this [GuiMessageTag] with [other].
 *
 * - Indicator colors are mixed, unless one of them is the SYSTEM color, in which case the other is used.
 * - The highest ordinal icon is used.
 * - Tag text is concatenated left-to-right.
 * - Log tags are comma-separated.
 */
operator fun GuiMessageTag.plus(other: GuiMessageTag): GuiMessageTag {
    val systemColor = GuiMessageTag.system().indicatorColor
    val newColor = when {
        indicatorColor == systemColor -> other.indicatorColor
        other.indicatorColor == systemColor -> indicatorColor
        else -> (indicatorColor + other.indicatorColor) / 2
    }

    val newIcon = combineIfNotNull(icon, other.icon) { first, second ->
        if (first.ordinal > second.ordinal) first else second
    }
    val newText = combineIfNotNull(text, other.text) { first, second ->
        first.copy()
            .append(VanillaComponent.literal(" "))
            .append(second)
    }
    val newLogTag = combineIfNotNull(logTag, other.logTag) { first, second ->
        "$first, $second"
    }

    return GuiMessageTag(newColor, newIcon, newText, newLogTag)
}

private inline fun <T : Any> combineIfNotNull(first: T?, second: T?, combinator: (T, T) -> T) =
    when {
        first == null -> second
        second == null -> first
        else -> combinator(first, second)
    }