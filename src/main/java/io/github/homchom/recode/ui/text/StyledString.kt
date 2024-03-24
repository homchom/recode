package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentIteratorType
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.minecraft.client.gui.Font

/**
 * A [Char] with a [Style].
 */
data class StyledChar(val char: Char, val style: Style)

/**
 * A [ComponentLike] [String]-like collection consisting purely of literal text.
 *
 * @see components
 * @see string
 */
class StyledString private constructor(
    private val text: Component,
    override val size: Int
) : AbstractCollection<StyledChar>(), ComponentLike by text {
    /**
     * A flat [Sequence] of the object's [Component]s.
     *
     * @see Component.asFlatSequence
     */
    val components = text.asFlatSequence().map { it as TextComponent }

    /**
     * The plain text representation of the object.
     */
    val string get() = asComponent().plainText

    constructor(text: ComponentLike) : this(
        text.asComponent().also { component ->
            for (comp in component) {
                if (comp !is TextComponent) {
                    throw IllegalArgumentException("StyledString must only consist of TextComponents")
                }
            }
        },
        text.asComponent().iterable(ComponentIteratorType.DEPTH_FIRST)
            .sumOf { (it as TextComponent).content().length }
    )

    fun width(font: Font) = font.width(text.toVanilla())

    override fun iterator() = components
        .flatMap { component ->
            component.content().map { StyledChar(it, component.style()) }
        }
        .iterator()

    companion object {
        /**
         * @see fromContents
         */
        fun fromContent(string: String, style: StyleWrapper) =
            StyledString(Component.text(string, style.build()))

        /**
         * @see fromContent
         */
        fun fromContents(vararg contents: Pair<String, StyleWrapper>) = StyledString(
            text {
                for ((string, style) in contents) literal(string, style)
            }.asComponent(),
            contents.sumOf { it.first.length }
        )
    }
}