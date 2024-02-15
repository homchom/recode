@file:JvmName("FormattedCharSequenceTransformations")

package io.github.homchom.recode.render.text

import net.minecraft.util.FormattedCharSequence

/**
 * @see CharSequence.subSequence
 */
fun FormattedCharSequence.subSequence(startIndex: Int, endIndex: Int) = FormattedCharSequence { sink ->
    acceptWithAbsoluteIndex { index, style, codePoint ->
        if (index in startIndex..<endIndex) {
            sink.accept(index - startIndex, style, codePoint)
        } else true
    }
}

/**
 * @see CharSequence.subSequence
 */
fun FormattedCharSequence.subSequence(range: IntRange) = subSequence(range.first, range.last + 1)