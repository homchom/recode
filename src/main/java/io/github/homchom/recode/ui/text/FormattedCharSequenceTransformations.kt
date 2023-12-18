@file:JvmName("FormattedCharSequenceTransformations")

package io.github.homchom.recode.ui.text

import net.minecraft.util.FormattedCharSequence

/**
 * @see CharSequence.subSequence
 */
fun FormattedCharSequence.subSequence(startIndex: Int, endIndex: Int) = FormattedCharSequence { sink ->
    var index = 0
    var adjustedIndex = 0
    accept { _, style, codePoint ->
        if (index++ in startIndex..<endIndex) {
            sink.accept(adjustedIndex++, style, codePoint)
        } else true
    }
}

/**
 * @see CharSequence.subSequence
 */
fun FormattedCharSequence.subSequence(range: IntRange) = subSequence(range.first, range.last)