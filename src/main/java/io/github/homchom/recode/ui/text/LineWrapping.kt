package io.github.homchom.recode.ui.text

import io.github.homchom.recode.mc
import io.github.homchom.recode.mixin.ui.FontInvoker
import io.github.homchom.recode.util.math.squared
import net.kyori.adventure.text.format.TextDecoration
import kotlin.math.sqrt

fun StyledString.wrapSquare(): List<StyledString> {
    val square = width(mc.font) * mc.font.lineHeight.toDouble()
    return wrap(sqrt(square).toFloat())
}

// TODO: can this be optimized further? https://github.com/jaroslov/knuth-plass-thoughts/blob/master/plass.md#further-optimizations--features
fun StyledString.wrap(idealWidth: Float, maxWidth: Float = Float.POSITIVE_INFINITY): List<StyledString> {
    // define local classes
    data class LineBreak(val score: Float, val nextIndex: Int)
    data class SubProblem(
        val index: Int,
        val width: Float,
        val spaceWidth: Float,
        var lineBreak: LineBreak? = null
    )

    // first, memoize by breakpoints (spaces) and calculate widths
    val memo = buildList {
        val fontInvoker = mc.font as FontInvoker

        var first = 0
        var width = 0f
        for ((last, styledChar) in this@wrap.withIndex()) {
            val fontID = styledChar.style.font()?.toResourceLocation() ?: VanillaStyle.DEFAULT_FONT
            val fontSet = fontInvoker.invokeGetFontSet(fontID)
            val isBold = styledChar.style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE
            val charWidth = fontSet.getGlyphInfo(styledChar.char.code, false).getAdvance(isBold)
            width += charWidth

            if (styledChar.char == ' ') {
                add(SubProblem(first, width, charWidth))
                first = last + 1
                width = 0f
            }
        }
    }

    // we want the lowest possible score, to minimize raggedness
    fun score(width: Float) = (width - idealWidth).squared()

    fun knuthPlass(index: Int): LineBreak {
        val entry = memo[index]

        // initially, a line in a sub-problem is just the leftmost word
        var lineWidth = entry.width

        var bestScore = score(lineWidth)
        var bestNextLineIndex = index + 1
        for (nextIndex in (index + 1)..memo.lastIndex) {
            val next = memo[nextIndex]

            if (lineWidth + next.width >= maxWidth) break
            lineWidth += next.width + next.spaceWidth
            val score = score(lineWidth)

            // solve the next sub-problem if we haven't already
            val lineBreak = next.lineBreak ?: knuthPlass(nextIndex)

            if (score + lineBreak.score < bestScore) {
                bestScore = score + lineBreak.score
                bestNextLineIndex = nextIndex
            }
        }

        // the last line of the paragraph shouldn't contribute to the score
        val newScore = if (bestNextLineIndex == memo.size) 0f else bestScore

        return LineBreak(newScore, bestNextLineIndex).also { entry.lineBreak = it }
    }

    knuthPlass(0) // run the algorithm

    // fold on the memo to return the final list
    val splitIndices = buildList {
        var index = 0
        while (true) {
            val nextLineIndex = memo[index].lineBreak?.nextIndex
                ?: throw IllegalArgumentException("maxWidth is impossibly small")
            if (nextLineIndex == memo.size) break

            // minus 1 to convert trailing spaces to leading spaces
            add(memo[nextLineIndex].index - 1)
            index = nextLineIndex
        }
    }
    return splitAt(*splitIndices.toIntArray()).map { str ->
        // drop leading spaces
        str.dropWhile { it.char == ' ' }
    }
}