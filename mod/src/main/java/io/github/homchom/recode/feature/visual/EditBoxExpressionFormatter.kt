package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.hypercube.HypercubeCommandAliases
import io.github.homchom.recode.render.text.formattedCharSequence
import io.github.homchom.recode.render.text.subSequence
import io.github.homchom.recode.util.regex.regex
import net.minecraft.util.FormattedCharSequence
import kotlin.math.max
import kotlin.math.min

private val highlightedCommands = listOf(
    CommandInfo(HypercubeCommandAliases.NUMBER, hasCount = true, parseMiniMessage = false),
    CommandInfo(HypercubeCommandAliases.STRING, hasCount = true, parseMiniMessage = false),
    CommandInfo(HypercubeCommandAliases.TEXT, hasCount = true),
    CommandInfo(HypercubeCommandAliases.VARIABLE, hasCount = true, parseMiniMessage = false),
    CommandInfo(HypercubeCommandAliases.ITEM_NAME),
    CommandInfo(HypercubeCommandAliases.ITEM_LORE_ADD),
    CommandInfo(HypercubeCommandAliases.ITEM_LORE_SET, highlightedArgumentIndex = 1),
    CommandInfo(HypercubeCommandAliases.ITEM_LORE_INSERT, highlightedArgumentIndex = 1),
    CommandInfo(HypercubeCommandAliases.PLOT_NAME),
    CommandInfo(HypercubeCommandAliases.RELORE)
)

/**
 * An object that retrofits [net.minecraft.client.gui.components.EditBox] for expression highlighting.
 *
 * @param formatCommands Whether to format commands for highlighting.
 * @param formatValues Should return `true` to parse MiniMessage when formatting values, `false` to not,
 * or `null` to not format values at all.
 *
 * @see format
 * @see ExpressionHighlighter
 */
class EditBoxExpressionFormatter(
    private val formatCommands: Boolean,
    private val formatValues: () -> Boolean?
) {
    private val highlighter = ExpressionHighlighter()

    /**
     * Formats [chatInput] for highlighting.
     *
     * @param partialParentFormat The partial [FormattedCharSequence] of the parent formatter.
     * @param partialRange The range of [partialParentFormat] within the complete sequence.
     *
     * @see [net.minecraft.client.gui.components.EditBox.setFormatter]
     */
    fun format(
        chatInput: String,
        partialParentFormat: FormattedCharSequence,
        partialRange: IntRange
    ): HighlightedExpression? {
        // highlight commands
        if (formatCommands && chatInput.startsWith('/')) {
            val (command, startIndex) = highlightedCommands.firstNotNullOfOrNull { info ->
                val match = info.prefix.matchAt(chatInput, 1)
                match?.let { info to it.value.length + 1 }
            } ?: return null
            return formatCommand(chatInput, startIndex, partialParentFormat, partialRange, command)
        }

        // highlight values
        formatValues()?.let { parseMiniMessage ->
            val highlighted = highlighter.highlightString(
                chatInput,
                parseMiniMessage
            )
            val subSequence = highlighted.text.subSequence(partialRange)
            return HighlightedExpression(subSequence, highlighted.preview)
        }

        return null
    }

    private fun formatCommand(
        chatInput: String,
        index: Int,
        partialParentFormat: FormattedCharSequence,
        partialRange: IntRange,
        info: CommandInfo
    ): HighlightedExpression? {
        var startIndex = index
        var endIndex = chatInput.length
        if (startIndex > chatInput.lastIndex) return null

        if (info.highlightedArgumentIndex > 0) {
            val regex = leadingArgumentsRegex(info.highlightedArgumentIndex)
            regex.find(chatInput, startIndex)?.let { match ->
                startIndex = match.range.last + 1
                if (startIndex > chatInput.lastIndex) return null
            }
        }
        if (info.hasCount) {
            countRegex.find(chatInput, startIndex)?.let { match ->
                endIndex = match.range.first
            }
        }

        val highlighted = highlighter.highlightString(
            chatInput.substring(startIndex, endIndex),
            info.parseMiniMessage
        )

        // combine highlight and partial parent format
        val combined = formattedCharSequence {
            val partialStart = partialRange.first
            val partialEnd = partialRange.last + 1

            if (partialStart < startIndex) append(partialParentFormat.subSequence(
                0,
                min(startIndex, partialEnd) - partialStart
            ))

            if (startIndex in partialRange || endIndex - 1 in partialRange) {
                append(highlighted.text.subSequence(
                    max(startIndex, partialStart) - startIndex,
                    min(endIndex, partialEnd) - startIndex
                ))
            }

            if (partialEnd > endIndex) append(partialParentFormat.subSequence(
                max(endIndex, partialStart) - partialStart,
                partialEnd - partialStart
            ))
        }

        return HighlightedExpression(combined, highlighted.preview)
    }
}

private data class CommandInfo(
    val prefix: Regex,
    val highlightedArgumentIndex: Int = 0,
    val hasCount: Boolean = false,
    val parseMiniMessage: Boolean = true
)

private val countRegex = regex {
    space
    digit.oneOrMore()
    end
}

private fun leadingArgumentsRegex(highlightIndex: Int) = regex {
    group {
        none(" ").oneOrMore()
        space
    } * (highlightIndex - 1)
    none(" ").oneOrMore()
    space.optional()
}