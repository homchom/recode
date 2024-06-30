package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.TextComponent
import kotlin.math.max
import kotlin.math.min

private inline fun StyledString.transform(builder: TextBuilder.(Sequence<TextComponent>) -> Unit) =
    StyledString(text { builder(components) })

fun StyledString.drop(n: Int) = transform { components ->
    var index = 0
    for (component in components) {
        val length = component.content().length
        val difference = n - index
        if (difference < length) {
            literal(component.content().substring(difference))
        }
        index += length
    }
}

inline fun StyledString.dropWhile(predicate: (StyledChar) -> Boolean) =
    drop(indexOfFirst { !predicate(it) })

fun StyledString.substring(startIndex: Int, endIndex: Int = size) = transform { components ->
    var index = 0
    for (component in components) {
        if (index >= endIndex) return@transform
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
}

fun StyledString.splitAt(vararg indices: Int) = buildList {
    val queue = ArrayDeque(indices.sorted())
    queue += size
    var currentText = TextBuilder()

    var index = 0
    for (component in components) {
        var index2 = index
        val nextIndex = index + component.content().length

        while (true) {
            if (queue.first() > index) {
                val substring = component.content().substring(
                    index2,
                    min(component.content().length, queue.first() - index)
                )
                currentText.literal(substring, style(component.style()))
            }
            if (queue.first() >= nextIndex) break

            add(StyledString(currentText.build()))
            currentText = TextBuilder()
            index2 = queue.removeFirst()

            if (queue.first() == index) throw IllegalArgumentException("indices must be distinct")
        }

        index = nextIndex
    }
}