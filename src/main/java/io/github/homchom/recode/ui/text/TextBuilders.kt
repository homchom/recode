package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.ComponentLike

typealias TextScope = TextBuilder.() -> Unit

/**
 * Creates a translated [Component] with [key], [args], and [style].
 */
fun translatedText(
    key: String,
    style: StyleWrapper = style(),
    args: Array<out ComponentLike> = emptyArray()
): Component {
    return Component.translatable(key, style.build(), *args)
}

/**
 * Creates a [Component] with literal [contents] and [style].
 */
fun literalText(contents: Any, style: StyleWrapper = style()) =
    Component.text(contents.toString(), style.build())

/**
 * Creates an empty [Component] with [style].
 */
fun emptyText(style: StyleWrapper = style()) =
    Component.empty().style(style.build())

/**
 * Builds a [Component] by adding [style] to [root] and applying [builder].
 *
 * Use [translatedText] and [literalText] when applicable, as their output is more optimized. Also note
 * that some functions take a [net.minecraft.util.FormattedCharSequence]; in those cases you should build
 * a [formattedCharSequence] directly.
 *
 * @see TextBuilder
 */
inline fun text(
    style: StyleWrapper = style(),
    root: ComponentBuilder<*, *> = Component.text(),
    builder: TextScope
): Component {
    return root.style(style.build())
        .let(::TextBuilder)
        .apply(builder)
        .build()
}

/**
 * @see translate
 * @see literal
 *
 * @see text
 */
@JvmInline
value class TextBuilder(val raw: ComponentBuilder<*, *> = Component.text()) {
    fun build(): Component = raw.build()

    /**
     * Appends [translatedText] with [key], [args], and [style] and applies [builder] to it.
     */
    inline fun translate(
        key: String,
        style: StyleWrapper = style(),
        args: Array<out ComponentLike> = emptyArray(),
        builder: TextScope = {}
    ) {
        raw.append(text(style, Component.translatable(key, *args).toBuilder(), builder))
    }

    /**
     * Appends [literalText] with [string] and [style] and applies [builder] to it.
     */
    inline fun literal(
        string: String,
        style: StyleWrapper = style(),
        builder: TextScope = {}
    ) {
        raw.append(text(style, Component.text(string).toBuilder(), builder))
    }

    /**
     * Appends a pre-existing [component].
     */
    fun append(component: Component) {
        raw.append(component)
    }
}