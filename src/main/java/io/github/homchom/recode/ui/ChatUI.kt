@file:JvmName("ChatUI")

package io.github.homchom.recode.ui

import io.github.homchom.recode.ui.text.VanillaComponent
import net.minecraft.client.GuiMessageTag

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