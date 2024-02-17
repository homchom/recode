@file:JvmName("FormattedCharSequenceExtensions")

package io.github.homchom.recode.render.text

import io.github.homchom.recode.util.std.fromCodePoint
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.FormattedCharSink

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