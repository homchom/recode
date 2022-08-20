@file:JvmName("Matchable")

package io.github.homchom.recode.util

import io.github.homchom.recode.event.Trial
import io.github.homchom.recode.event.runTrial

fun <T, R : Any> match(default: () -> R, vararg matchers: Trial.(T) -> R): Matcher<T, R> =
    MatcherArray(default, matchers)

interface Matcher<T, R> {
    fun match(input: T): R
}

private class MatcherArray<T, R : Any>(
    private val default: () -> R,
    private val matchers: Array<out Trial.(T) -> R>
) : Matcher<T, R> {
    override fun match(input: T) = matchers.firstNotNullOfOrNull { matcher ->
        runTrial { matcher(input) }
    } ?: default()
}