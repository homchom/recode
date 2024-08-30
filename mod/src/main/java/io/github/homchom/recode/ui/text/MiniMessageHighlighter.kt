package io.github.homchom.recode.ui.text

import io.github.homchom.recode.util.std.interpolate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.internal.parser.node.TagNode
import net.kyori.adventure.text.minimessage.internal.parser.node.ValueNode
import net.kyori.adventure.text.minimessage.tag.Inserting
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.kyori.adventure.text.minimessage.tree.Node

/**
 * A parser that performs syntax highlighting on [MiniMessage] expressions.
 *
 * @see highlight
 */
class MiniMessageHighlighter(private val standardTags: TagResolver) {
    // a resolver for tags that determine their own style
    private val nonLiteralTags = TagResolver.resolver(
        StandardTags.color(),
        StandardTags.decorations().without(
            StandardTags.decorations(TextDecoration.OBFUSCATED)
        ),
        StandardTags.reset(),
        StandardTags.gradient(),
        StandardTags.rainbow()
    )

    private val instance = MiniMessage.builder().run {
        tags(object : TagResolver {
            override fun resolve(name: String, arguments: ArgumentQueue, ctx: Context): Tag? {
                nonLiteralTags.resolve(name, arguments, ctx)?.let { return it }
                if (standardTags.has(name)) return LiteralTag
                return null
            }

            override fun has(name: String) = standardTags.has(name) || nonLiteralTags.has(name)
        })
        build()
    }

    private fun overrideStyle(tagName: String, tag: Tag) = when {
        StandardTags.gradient().has(tagName) || StandardTags.rainbow().has(tagName) -> "white"
        tag == LiteralTag -> "gray"
        else -> null
    }

    /**
     * Performs [MiniMessage] syntax highlighting on [input].
     *
     * - Color, decoration, and font tags are styled with their respective style insertions.
     * `<obfuscated>` is **not** supported.
     * - All other valid tags are treated as "literals" and colored gray.
     */
    @Suppress("UnstableApiUsage") // TODO: open issue at adventure github
    fun highlight(input: String): Component {
        // handle the special case of inputs with <reset> parser directives
        // TODO: determine if there is a better way to do this
        val splitByResets = input.split("<reset>")
        if (splitByResets.size > 1) return text {
            for (index in 0..<splitByResets.lastIndex) {
                append(highlight(splitByResets[index]))
                literal("<reset>")
            }
            append(highlight(splitByResets.last()))
        }.asComponent()

        fun TagNode.input() = with(token()) { input.substring(startIndex(), endIndex()) }
        fun ValueNode.input() = with(token()) { input.substring(startIndex(), endIndex()) }

        // we highlight by building a second MiniMessage expression and deserializing it
        // this is easier than reimplementing all tag behavior, e.g. Modifying
        val newInput = StringBuilder(input.length)
        var inputIndex = 0

        fun buildNewInput(node: Node) {
            val style = when (node) {
                is TagNode -> {
                    val tagString = node.input()
                    inputIndex += tagString.length
                    val styleOverride = overrideStyle(node.name(), node.tag())
                        ?: node.parts().joinToString(":")
                    newInput.appendEscapedTag(tagString, styleOverride)
                    newInput.append(tagString)
                    styleOverride
                }
                is ValueNode -> {
                    val value = node.input()
                    inputIndex += value.length
                    newInput.append(instance.escapeAll(value))
                    null
                }
                else -> null
            }

            for (child in node.children()) buildNewInput(child)

            // handle closing tags (which are optional)
            if (node is TagNode) {
                val closing = "</${node.name()}>"
                if (input.startsWith(closing, inputIndex)) {
                    inputIndex += closing.length
                    newInput.append(closing)
                    newInput.appendEscapedTag(closing, style!!)
                }
            }
        }

        val root = instance.deserializeToTree(input)
        buildNewInput(root)
        return instance.deserialize(newInput.toString())
    }

    private fun StringBuilder.appendEscapedTag(tagString: String, style: String) {
        interpolate("<", style, ">", instance.escapeAll(tagString), "</", style, ">")
    }

    // an empty tag and a marker used to identify literals
    private data object LiteralTag : Inserting {
        override fun value() = Component.empty()
    }
}