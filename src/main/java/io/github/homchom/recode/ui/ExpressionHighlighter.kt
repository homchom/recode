package io.github.homchom.recode.ui

import io.github.homchom.recode.render.HexColor
import io.github.homchom.recode.util.Computation
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

object ExpressionHighlighter {
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

    private val codeRegex = regex {
        group {
            str("%")
            any("a-zA-Z").oneOrMore()
            str("(").optional()
        }
        or; str(")")
        or; end
    }

    // TODO: new color scheme?
    private val colors = listOf(
        0xffffff,
        0xffd600,
        0x33ff00,
        0x00ffe0,
        0x5e77f7,
        0xca64fa,
        0xff4242
    )

    fun highlightString(string: String, parseMiniMessage: Boolean = false): Computation<Component, String> {
        val result = if (parseMiniMessage) {
            object : HighlightBuilder {
                private val builder = StringBuilder()

                override fun append(text: String, depth: Int, depthIncreased: Boolean) {
                    val tag = if (depthIncreased) "<color:${colorAt(depth)}>" else "</color>"
                    builder.append("$tag$text")
                }

                override fun build() = MiniMessage.miniMessage().deserializeToNative(builder.toString())
            }
        } else {
            object : HighlightBuilder {
                private val builder = Component.empty()

                override fun append(text: String, depth: Int, depthIncreased: Boolean) {
                    val style = Style.EMPTY.withColor(colors[depth % colors.size])
                    builder.append(Component.literal(text).withStyle(style))
                }

                override fun build(): Component = builder
            }
        }

        var sliceStart = 0
        var depth = 0
        var code = ""
        for (match in codeRegex.findAll(string)) {
            val depthIncreased = code.endsWith('(') || sliceStart == 0
            result.append(string.substring(sliceStart, match.range.first), depth, depthIncreased)

            code = match.value
            if (code.length > 1) {
                val codeName = if (code.endsWith('(')) {
                    code.substring(1, code.lastIndex)
                } else {
                    code.drop(1)
                }
                if (codeName !in codes) return Computation.Failure("Invalid text code: %$codeName")
            }

            if (code == ")") {
                if (depth > 0) depth--
            } else depth++
            result.append(string.substring(match.range), depth, code != ")")
            if (code.endsWith('(')) depth++ else {
                if (depth > 0) depth--
            }

            sliceStart = match.range.last + 1
        }

        return Computation.Success(result.build())
    }

    private fun colorAt(depth: Int) = HexColor(colors[depth % colors.size])

    private interface HighlightBuilder {
        fun append(text: String, depth: Int, depthIncreased: Boolean)

        fun build(): Component
    }
}