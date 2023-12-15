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

/**
 * An object that highlights and caches DF value and MiniMessage expressions.
 *
 * @see runHighlighting
 */
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

    private val highlightedCommands = buildList {
        fun addGroup(
            group: CommandAliasGroup,
            highlightedArgumentIndex: Int = 0,
            hasCount: Boolean = false,
            parseMiniMessage: Boolean = true
        ) {
            addAll(group.map { prefix ->
                CommandInfo(prefix, highlightedArgumentIndex, hasCount, parseMiniMessage)
            })
        }

        addGroup(CommandAliasGroup.NUMBER, hasCount = true, parseMiniMessage = false)
        addGroup(CommandAliasGroup.STRING, hasCount = true, parseMiniMessage = false)
        addGroup(CommandAliasGroup.TEXT, hasCount = true)
        addGroup(CommandAliasGroup.VARIABLE, hasCount = true, parseMiniMessage = false)
        addGroup(CommandAliasGroup.ITEM_NAME)
        addGroup(CommandAliasGroup.ITEM_LORE_ADD)
        addGroup(CommandAliasGroup.ITEM_LORE_SET, highlightedArgumentIndex = 1)
        addGroup(CommandAliasGroup.PLOT_NAME)
        addGroup(CommandAliasGroup.RELORE)
    }

    private data class CommandInfo(
        val prefix: String,
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
        group {
            none(" ").oneOrMore()
            space
        } * (highlightIndex - 1)
        none(" ").oneOrMore()
        space.optional()
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
            val command = highlightedCommands.firstNotNullOfOrNull { info ->
                info.takeIf { chatInput.startsWith(info.prefix, 1) }
            } ?: return null
            return highlightCommand(chatInput, formatted, command)
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
        info: CommandInfo
    ): HighlightedExpression? {
        var startIndex = info.prefix.length + 2
        var endIndex = input.length
        if (startIndex > input.lastIndex) return null

        if (info.highlightedArgumentIndex > 0) {
            val regex = leadingArgumentsRegex(info.highlightedArgumentIndex)
            regex.find(input, startIndex)?.let { match ->
                startIndex = match.range.last + 1
                if (startIndex > input.lastIndex) return null
            }
        }
        if (info.hasCount) {
            countRegex.find(input, startIndex)?.let { match ->
                endIndex = match.range.first
            }
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