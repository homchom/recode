package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.hypercube.DFMiniMessageTags
import io.github.homchom.recode.hypercube.dfMiniMessage
import io.github.homchom.recode.ui.text.MiniMessageHighlighter
import io.github.homchom.recode.ui.text.TextBuilder
import io.github.homchom.recode.ui.text.style
import io.github.homchom.recode.ui.text.toFormattedCharSequence
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.TextComponent
import net.minecraft.util.FormattedCharSequence

data class HighlightedExpression(val text: FormattedCharSequence, val preview: FormattedCharSequence?)

private val codes = setOf(
    "default",
    "selected",
    "uuid",
    "var",
    "math",
    "damager",
    "killer",
    "shooter",
    "victim",
    "projectile",
    "random",
    "round",
    "index",
    "entry"
)

// TODO: new color scheme?
private val colors = listOf(
    0xffd600,
    0x33ff00,
    0x00ffe0,
    0x5e77f7,
    0xca64fa,
    0xff4242
)

/**
 * An object that highlights and previews DF value and MiniMessage expressions.
 *
 * For specialized [net.minecraft.client.gui.components.EditBox] highlighting,
 * use [EditBoxExpressionFormatter].
 *
 * @see highlightString
 */
class ExpressionHighlighter {
    private var cachedString = ""
    private var cachedParseMiniMessage = true
    private var cachedHighlight = HighlightedExpression(FormattedCharSequence.EMPTY, null)

    private val miniMessage = dfMiniMessage
    private val miniMessageHighlighter = MiniMessageHighlighter(DFMiniMessageTags.all)

    private val codeRegex = regex {
        group {
            str("%")
            any("a-zA-Z").oneOrMore()
            str("(").optional()
        }
        or; str(")")
        or; end
    }

    fun highlightString(string: String, parseMiniMessage: Boolean = true): HighlightedExpression {
        if (string != cachedString || parseMiniMessage != cachedParseMiniMessage) {
            cachedString = string
            cachedParseMiniMessage = parseMiniMessage
            cachedHighlight = highlightUncached(string, parseMiniMessage)
        }

        return cachedHighlight
    }

    private fun highlightUncached(string: String, parseMiniMessage: Boolean = true): HighlightedExpression {
        val builder = TextBuilder()
        var sliceStart = 0
        var depth = 0

        for (match in codeRegex.findAll(string)) {
            builder.literal(string.substring(sliceStart, match.range.first), styleAt(depth))

            val code = match.value
            if (code == ")") {
                if (depth > 0) depth--
            } else depth++

            val style = if (code.length > 1 && code.drop(1).removeSuffix("(") !in codes) {
                style().red()
            } else {
                styleAt(depth)
            }
            builder.literal(string.substring(match.range), style)

            if (code.endsWith('(')) depth++ else {
                if (depth > 0) depth--
            }

            sliceStart = match.range.last + 1
        }

        if (parseMiniMessage) builder.raw.mapChildren { text ->
            if (text is TextComponent && text.style().isEmpty) {
                miniMessageHighlighter.highlight(text.content()) as BuildableComponent<*, *>
            } else {
                text
            }
        }

        val text = builder.build().toFormattedCharSequence(false)
        val preview = if (parseMiniMessage) {
            miniMessage.deserialize(string).toFormattedCharSequence()
        } else null
        return HighlightedExpression(text, preview)
    }

    private fun styleAt(depth: Int) = if (depth == 0) {
        style()
    } else {
        style().color(colors[depth - 1 % colors.size])
    }
}