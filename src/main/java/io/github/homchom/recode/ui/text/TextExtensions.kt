@file:JvmName("TextExtensions")

package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentIteratorType
import net.kyori.adventure.text.format.Style

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
 * Returns a flattened [List] of this [Component]'s nodes, where parent and child styles are recursively merged.
 *
 * @see asFlatSequence
 */
fun Component.toFlatList() = buildList {
    val queue = ArrayDeque<Component>(1)
    queue += this@toFlatList
    do {
        add(queue.removeFirst())
        for (child in children()) add(mergeStyle(child))
    } while (queue.isNotEmpty())
}

/**
 * Returns a flattened [Sequence] of this [Component]'s nodes, where parent and child styles are recursively merged.
 *
 * @see toFlatList
 */
fun Component.asFlatSequence(): Sequence<Component> = sequence {
    yield(this@asFlatSequence)
    for (child in children()) {
        val merged = child.mergeStyle(this@asFlatSequence)
        yieldAll(merged.asFlatSequence())
    }
}

/**
 * @see Component.iterator
 */
operator fun Component.iterator(): Iterator<Component> = iterator(ComponentIteratorType.DEPTH_FIRST)

/**
 * @return Whether this [Component] equals [other] when flattened.
 */
infix fun Component.looksLike(other: Component) = toFlatList() == other.toFlatList()

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