package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentIteratorType
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style

data class StyledChar(val char: Char, val style: Style)

class StyledString private constructor(
    private val text: Component,
    override val size: Int
) : AbstractCollection<StyledChar>(), ComponentLike by text {
    constructor(vararg contents: Pair<String, StyleWrapper>) : this(
        text {
            for ((string, style) in contents) literal(string, style)
        }.asComponent(),
        contents.sumOf { it.first.length }
    )

    constructor(text: Component) : this(
        text.apply {
            for (component in iterator(ComponentIteratorType.DEPTH_FIRST)) {
                if (component !is TextComponent) {
                    throw IllegalArgumentException("StyledString must only consist of TextComponents")
                }
            }
        },
        text.iterable(ComponentIteratorType.DEPTH_FIRST)
            .sumOf { (it as TextComponent).content().length }
    )

    override fun iterator() = iterator {
        for (component in text.iterator(ComponentIteratorType.DEPTH_FIRST)) {
            for (char in (component as TextComponent).content()) {
                yield(StyledChar(char, component.style()))
            }
        }
    }
}