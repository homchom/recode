package io.github.homchom.recode.matcher

import net.minecraft.network.chat.Component

inline fun <reified T> Component.matchType(matcher: (T) -> Boolean) = this is T && matcher(this as T)

fun Component.matchShallow(matcher: (Component) -> Boolean): Boolean = matcher(this) || this.matchSibling(matcher)
fun Component.matchSibling(matcher: (Component) -> Boolean): Boolean = this.siblings.any(matcher)

fun Component.matchAnywhere(matcher: (Component) -> Boolean): Boolean {
    if (matcher(this)) return true

    var components = this.siblings.toMutableList()

    while (components.isNotEmpty()) {
        val oldComponents = components.toMutableList()
        components = mutableListOf()

        for (elem in oldComponents) {
            if (matcher(elem)) return true

            components.addAll(elem.siblings)
        }
    }

    return false
}

fun Component.orderedMatch(matchers: Array<(Component) -> Boolean>): Boolean {
    fun Component.matchRecursive(matcher: (Component) -> Boolean): Boolean = matcher(this) || this.siblings.any { it.matchRecursive(matcher) }

    var i = 0

    this.matchRecursive {
        val matched = matchers[i](it)
        if (matched) i++

        matched
    }
}