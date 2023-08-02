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

// TODO: replace builder with RegexPatternBuilder once it is stable
fun <T : Any> cachedRegex(builder: (T?) -> Regex) = cachePreviousAndNull(builder)