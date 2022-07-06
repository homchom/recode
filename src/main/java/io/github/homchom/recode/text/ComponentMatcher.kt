package io.github.homchom.recode.text

import net.minecraft.network.chat.Component

typealias Matcher = (Component) -> Boolean

inline fun <reified T : Component> Component.matchType(matcher: (T) -> Boolean) =
    this is T && matcher(this)

inline fun Component.matchShallow(matcher: Matcher) =
    matcher(this) || siblings.any(matcher)

fun Component.matchRecursive(matcher: Matcher): Boolean =
    matcher(this) || siblings.any { it.matchRecursive(matcher) }

fun Component.matchOrdered(vararg matchers: Matcher): Boolean {
    var i = 0
    return this.matchRecursive { text ->
        matchers[i](text).also { if (it) i++ }
    }
}