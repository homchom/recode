package io.github.homchom.recode.util

val MatchResult.namedGroupValues get() = RegexNamedGroupValueCollection(this)

class RegexNamedGroupValueCollection(private val match: MatchResult) : List<String> by match.groupValues {
    /**
     * @throws IllegalArgumentException
     * @see MatchNamedGroupCollection.get
     */
    operator fun get(name: String) = match.groups[name]?.value ?: ""
}

fun <T : Any> cachedRegexBuilder(builder: (T?) -> Regex) = cachePreviousAndNull(builder)