package io.github.homchom.recode.ui.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

// https://github.com/KyoriPowered/adventure/issues/998
/**
 * Escapes [MiniMessage] tags and escape characters in [input].
 *
 * @return The escaped string.
 */
fun MiniMessage.escapeAll(input: String) = serialize(Component.text(input))

/**
 * @return A new [TagResolver] that resolves like this TagResolver but excludes [disabledTags].
 */
fun TagResolver.without(vararg disabledTags: TagResolver) = object : TagResolver {
    override fun resolve(name: String, arguments: ArgumentQueue, ctx: Context): Tag? {
        if (disabledTags.any { it.has(name) }) return null
        return this@without.resolve(name, arguments, ctx)
    }

    override fun has(name: String): Boolean {
        if (disabledTags.any { it.has(name) }) return false
        return this@without.has(name)
    }
}