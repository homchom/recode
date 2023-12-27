package io.github.homchom.recode.util.regex

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