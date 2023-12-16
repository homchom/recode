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
    inline val standard get() = StandardTags.defaults()

    val space = TagResolver.resolver("space") { args, context ->
        val count = if (args.hasNext()) {
            args.pop().asInt().orElseThrow {
                context.newException("Count must be a number")
            }
        } else 1
        if (count < 0) throw context.newException("Count must be non-negative")
        Tag.inserting(literalText(" ".repeat(count)))
    }

    val all = TagResolver.resolver(standard, space)
}