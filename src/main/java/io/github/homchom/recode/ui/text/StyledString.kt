package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentIteratorType
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import kotlin.math.max
import kotlin.math.min

data class StyledChar(val char: Char, val style: Style)

class StyledString private constructor(
    private val text: Component,
    override val size: Int
) : AbstractCollection<StyledChar>(), ComponentLike by text {
    val components = text.asFlatSequence().map { it as TextComponent }

    constructor(vararg contents: Pair<String, StyleWrapper>) : this(
        text {
            for ((string, style) in contents) literal(string, style)
        }.asComponent(),
        contents.sumOf { it.first.length }
    )

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

    override fun iterator() = components
        .flatMap { component ->
            component.content().map { StyledChar(it, component.style()) }
        }
        .iterator()

    fun substring(startIndex: Int, endIndex: Int = size) = StyledString(text {
        var index = 0
        for (component in components) {
            if (index >= endIndex) return@text
            val nextIndex = index + component.content().length
            if (nextIndex > startIndex) {
                val substring = component.content().substring(
                    max(0, startIndex - index),
                    min(component.content().length, endIndex - index)
                )
                literal(substring, style(component.style()))
            }
            index = nextIndex
        }
    })
}