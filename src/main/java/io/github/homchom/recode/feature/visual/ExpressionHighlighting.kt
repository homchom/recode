package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.hypercube.CommandAliasGroup
import io.github.homchom.recode.hypercube.DFValueMeta
import io.github.homchom.recode.hypercube.dfMiniMessage
import io.github.homchom.recode.hypercube.dfValueMeta
import io.github.homchom.recode.ui.text.*
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.TextComponent
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

data class HighlightedExpression(val text: FormattedCharSequence, val preview: FormattedCharSequence?)

class ExpressionHighlighter {
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

    private val highlightedCommands = buildMap {
        fun putGroup(
            group: CommandAliasGroup,
            highlightedArgumentIndex: Int = 0,
            hasCount: Boolean = false,
            parseMiniMessage: Boolean = true
        ) {
            val info = CommandInfo(highlightedArgumentIndex, hasCount, parseMiniMessage)
            putAll(group.map { it to info })
        }

        putGroup(CommandAliasGroup.NUMBER, hasCount = true, parseMiniMessage = false)
        putGroup(CommandAliasGroup.STRING, hasCount = true, parseMiniMessage = false)
        putGroup(CommandAliasGroup.TEXT, hasCount = true)
        putGroup(CommandAliasGroup.VARIABLE, hasCount = true, parseMiniMessage = false)
        putGroup(CommandAliasGroup.ITEM_NAME)
        putGroup(CommandAliasGroup.ITEM_LORE_ADD)
        putGroup(CommandAliasGroup.ITEM_LORE_SET, highlightedArgumentIndex = 1)
        putGroup(CommandAliasGroup.PLOT_NAME)
        putGroup(CommandAliasGroup.RELORE)
    }

    private data class CommandInfo(
        val highlightedArgumentIndex: Int,
        val hasCount: Boolean,
        val parseMiniMessage: Boolean
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

    private val codeRegex = regex {
        group {
            str("%")
            any("a-zA-Z").oneOrMore()
            str("(").optional()
        }
        or; str(")")
        or; end
    }

    private var cachedInput = ""
    private var cachedHighlight = HighlightedExpression(FormattedCharSequence.EMPTY, null)

    private val countRegex = regex {
        space
        digit.oneOrMore()
        end
    }

    private fun leadingArgumentsRegex(highlightIndex: Int) = regex {
        start
        group {
            none(" ").oneOrMore()
            space
        } * (highlightIndex + 1)
    }

    fun runHighlighting(
        chatInput: String,
        formatted: FormattedCharSequence,
        player: Player
    ): HighlightedExpression? {
        if (cachedInput == chatInput) return cachedHighlight

        val highlight = highlight(chatInput, formatted, player.mainHandItem)
        if (highlight != null) {
            cachedInput = chatInput
            cachedHighlight = highlight
        }
        return highlight
    }

    private fun highlight(
        chatInput: String,
        formatted: FormattedCharSequence,
        mainHandItem: ItemStack
    ): HighlightedExpression? {
        // highlight commands
        if (chatInput.startsWith('/')) {
            val splitIndex = chatInput.indexOf(' ') + 1
            if (splitIndex != 0) {
                val command = highlightedCommands[chatInput.substring(1, splitIndex - 1)]
                if (command != null) {
                    return highlightCommand(chatInput, formatted, command, splitIndex)
                }
            }
            return null
        }

        // highlight values
        val valueMeta = mainHandItem.dfValueMeta()
        if (valueMeta is DFValueMeta.Primitive || valueMeta is DFValueMeta.Variable) {
            return highlightString(chatInput, valueMeta.type == "comp")
        }

        return null
    }

    private fun highlightString(string: String, parseMiniMessage: Boolean = true): HighlightedExpression {
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
                MiniMessageHighlighter.highlight(text.content()) as BuildableComponent<*, *>
            } else {
                text
            }
        }

        val text = builder.build().toFormattedCharSequence(false)
        val preview = if (parseMiniMessage) {
            dfMiniMessage.deserialize(string).toFormattedCharSequence()
        } else null
        return HighlightedExpression(text, preview)
    }

    private fun highlightCommand(
        input: String,
        formatted: FormattedCharSequence,
        info: CommandInfo,
        splitIndex: Int
    ): HighlightedExpression {
        var startIndex = splitIndex
        var endIndex = input.length

        if (info.highlightedArgumentIndex > 0) {
            val regex = leadingArgumentsRegex(info.highlightedArgumentIndex)
            val match = regex.find(input, startIndex)
            if (match != null) startIndex = match.range.last + 1
        }
        if (info.hasCount) {
            val match = countRegex.find(input, startIndex)
            if (match != null) endIndex = match.range.first
        }

        val highlighted = highlightString(input.substring(startIndex, endIndex), info.parseMiniMessage)
        val combined = formatted.replaceRange(startIndex..<endIndex, highlighted.text)
        return HighlightedExpression(combined, highlighted.preview)
    }

    private fun styleAt(depth: Int) = if (depth == 0) {
        style()
    } else {
        style().color(colors[depth - 1 % colors.size])
    }
}