package io.github.homchom.recode.util.regex

import io.github.homchom.recode.util.cachePreviousAndNull

/**
 * A list of matched group values by name.
 *
 * @see MatchResult.groupValues
 */
val MatchResult.namedGroupValues get() = RegexNamedGroupValueCollection(this)

/**
 * @see namedGroupValues
 */
class RegexNamedGroupValueCollection(private val match: MatchResult) : List<String> by match.groupValues {
    /**
     * @throws IllegalArgumentException
     * @see MatchNamedGroupCollection.get
     */
    operator fun get(name: String) = match.groups[name]?.value ?: ""
}

/**
 * @see regex
 * @see cachePreviousAndNull
 */
inline fun <T : Any> cachedRegex(crossinline builder: RegexPatternBuilder.(T?) -> Unit) =
    cachePreviousAndNull<T, _> { input ->
        regex { builder(input) }
    }