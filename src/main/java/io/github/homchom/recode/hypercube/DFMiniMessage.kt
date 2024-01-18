package io.github.homchom.recode.hypercube

import io.github.homchom.recode.ui.text.literalText
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

/**
 * A [MiniMessage] instance to match DiamondFire's MiniMessage behavior.
 */
val dfMiniMessage = MiniMessage.builder().run {
    tags(DFMiniMessageTags.all)
    build()
}

/**
 * An object providing access to DiamondFire's MiniMessage [TagResolver]s.
 *
 * @see dfMiniMessage
 * @see StandardTags
 */
@Suppress("MemberVisibilityCanBePrivate")
object DFMiniMessageTags {
    private const val MAX_REPETITION_COUNT = 32

    // TODO: remove this when adventure 5.15.0
    inline val standard get() = StandardTags.defaults()

    val space = repetitionTagResolver("space", " ")
    val repeatableNewline = repetitionTagResolver("newline", "\n")

    val all = TagResolver.resolver(standard, space, repeatableNewline)

    private fun repetitionTagResolver(name: String, literal: String) =
        TagResolver.resolver(name) { args, context ->
            val count = if (args.hasNext()) {
                args.pop().asInt().orElseThrow {
                    context.newException("Count must be a number")
                }
            } else 1

            val repeated = literal.repeat(count.coerceAtMost(MAX_REPETITION_COUNT))
            Tag.selfClosingInserting(literalText(repeated))
        }
}