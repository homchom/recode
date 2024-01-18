@file:JvmName("TextFunctions")

package io.github.homchom.recode.ui.text

import io.github.homchom.recode.util.std.fromCodePoint
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.FormattedCharSink

/**
 * @return A new [Component] created by merging this Component's style with [style], using [strategy] and [merges].
 */
fun Component.mergeStyle(
    style: Style,
    strategy: Style.Merge.Strategy = Style.Merge.Strategy.ALWAYS,
    merges: Set<Style.Merge> = Style.Merge.all()
): Component {
    return style(style().merge(style, strategy, merges))
}

/**
 * A shortcut for [Component.mergeStyle] with strategy [Style.Merge.Strategy.IF_ABSENT_ON_TARGET].
 */
fun Component.mergeStyleIfAbsent(style: Style, merges: Set<Style.Merge> = Style.Merge.all()) =
    mergeStyle(style, Style.Merge.Strategy.IF_ABSENT_ON_TARGET, merges)

/**
 * Returns a flattened [Sequence] of this [Component]'s nodes, where parent and child styles are recursively merged.
 */
fun Component.asFlatSequence(): Sequence<Component> = sequence {
    yield(this@asFlatSequence)
    for (child in children()) {
        val merged = mergeStyle(child)
        yieldAll(merged.asFlatSequence())
    }
}

/**
 * @return Whether this [Component] equals [other] when flattened.
 */
infix fun Component.looksLike(other: Component) = asFlatSequence() == other.asFlatSequence()

/**
 * @return Whether this [FormattedCharSequence] and [other] yield the same styles and code points.
 */
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

/**
 * @return A plain text representation of this [Component].
 *
 * @see LegacyCodeRemover.plainText
 */
val Component.plainText get() = LegacyCodeRemover.plainText(this)

/**
 * @return Whether this [Component]'s [plainText] equals [string].
 */
fun Component.equalsPlain(string: String) = plainText == string

/**
 * @return Whether this [Component]'s [plainText] equals [other]'s plain text.
 */
fun Component.equalsPlain(other: Component) = equalsPlain(other.plainText)

/**
 * Attempts to match [text]'s entire [plainText] against this [Regex] pattern.
 */
fun Regex.matchEntirePlain(text: Component) = matchEntire(text.plainText)

/**
 * @return Whether [text]'s entire [plainText] matches this [Regex] pattern.
 */
fun Regex.matchesPlain(text: Component) = matches(text.plainText)

/**
 * [FormattedCharSequence.accept]s this [FormattedCharSequence], adjusting the `index` parameter
 * passed to [sink] to be absolute instead of relative. Surrogate pairs are handled but not validated.
 */
fun FormattedCharSequence.acceptWithAbsoluteIndex(sink: FormattedCharSink): Boolean {
    var absoluteIndex = 0
    return accept { _, style, codePoint ->
        val shouldContinue = sink.accept(absoluteIndex++, style, codePoint)
        if (String.fromCodePoint(codePoint)[0].isHighSurrogate()) {
            absoluteIndex++
        }
        shouldContinue
    }
}