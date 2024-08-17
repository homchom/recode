package io.github.homchom.recode.util.regex

import io.github.homchom.recode.util.MutableCase

/**
 * Creates a [RegexFactory] of [Regex] patterns based on a given input. For performance, patterns are
 * cached when the input is `null` or consecutively equal.
 *
 * @see regex
 */
fun <T> dynamicRegex(builder: RegexPatternBuilder.(T) -> Unit): RegexFactory<T> =
    NullCachedRegexFactory(builder)

/**
 * @see dynamicRegex
 */
sealed interface RegexFactory<T> {
    operator fun invoke(input: T): Regex
}

/**
 * @see dynamicRegex
 */
private class NullCachedRegexFactory<T>(
    private val builder: RegexPatternBuilder.(input: T) -> Unit
) : RegexFactory<T> {
    private var prevInput: MutableCase<T>? = null
    private lateinit var prevResult: Regex
    private lateinit var nullDefault: Regex

    override fun invoke(input: T): Regex {
        if (input == null) {
            if (!::nullDefault.isInitialized) {
                nullDefault = regex { builder(input) }
            }
            return nullDefault
        }

        prevInput?.let { (content) ->
            if (input == content) return prevResult
        }
        prevInput = MutableCase(input)
        prevResult = regex { builder(input) }
        return prevResult
    }
}