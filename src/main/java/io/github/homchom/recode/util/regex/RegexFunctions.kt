package io.github.homchom.recode.util.regex

import io.github.homchom.recode.util.cachePreviousAndNull

val MatchResult.namedGroupValues get() = RegexNamedGroupValueCollection(this)

class RegexNamedGroupValueCollection(private val match: MatchResult) : List<String> by match.groupValues {
    /**
     * @throws IllegalArgumentException
     * @see MatchNamedGroupCollection.get
     */
    operator fun get(name: String) = match.groups[name]?.value ?: ""
}

inline fun <T : Any> cachedRegex(crossinline builder: RegexPatternBuilder.(T?) -> Unit) =
    cachePreviousAndNull<T, _> { input ->
        regex { builder(input) }
    }