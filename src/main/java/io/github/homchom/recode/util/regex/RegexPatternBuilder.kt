package io.github.homchom.recode.util.regex

import io.github.homchom.recode.logInfo

/**
 * @throws RegexConstructException
 */
@RegexUnproven
inline fun regex(
    useUnixLines: Boolean = false,
    useCanonicalEquivalence: Boolean = false,
    builder: RegexPatternBuilder.() -> Unit
): Regex {
    val pattern = RegexPatternBuilder().apply(builder).build()
    logInfo("built regex pattern $pattern")
    val options = buildSet {
        if (useUnixLines) add(RegexOption.UNIX_LINES)
        if (useCanonicalEquivalence) add(RegexOption.CANON_EQ)
    }
    return Regex(pattern, options)
}

@RegexUnproven
class RegexPatternBuilder {
    private val elements = mutableListOf<RegexPattern>()

    operator fun <T : RegexPattern> T.unaryPlus() = also { pattern ->
        ensureGrouped(true)
        elements += pattern
    }

    fun build(): String = elements.joinToString("")

    // literals

    fun literal(character: Char) = raw(character, true)

    fun literal(string: String) = raw(string, RegexPattern.Grouping.OPTIONAL, true)

    // character classes

    val wordChar get() = raw("\\w")
    val digit get() = raw("\\d")
    val any get() = raw(".")

    // groups and alternation

    fun range(characters: CharRange) = rawRange(characters.first, characters.last)

    fun all(builder: RegexPatternBuilder.() -> Unit) = rawGroup(builder)

    fun any(vararg expressions: RegexConstruct): QuantifiableRegexPattern {
        val isCharGroup = expressions.all { it.isCharacterGroupEligible }

        val grouping: RegexPattern.Grouping
        val separator: String
        if (isCharGroup) {
            grouping = RegexPattern.Grouping.CHARACTER
            separator = ""
        } else {
            grouping = RegexPattern.Grouping.OPTIONAL
            separator = "|"
        }

        return alternation(expressions, separator, grouping)
    }

    fun none(vararg characters: RegexCharacter): QuantifiableRegexPattern {
        val isCharGroup = characters.all { it.isCharacterGroupEligible }
        if (!isCharGroup) throw RegexConstructException(
            "branches of a negated alternation group cannot themselves be grouped",
            IllegalArgumentException()
        )
        return alternation(characters, "", RegexPattern.Grouping.NEGATED_CHARACTER)
    }

    private fun alternation(
        constructs: Array<out RegexConstruct>,
        separator: String,
        grouping: RegexPattern.Grouping
    ): QuantifiableRegexPattern {
        return raw(constructs.joinToString(separator), grouping)
            .apply { ensureGrouped() }
    }
}

@RequiresOptIn("This regex API has not been laboriously tested for potential edge issues. If this is " +
        "used and something breaks, double check here for API bugs")
annotation class RegexUnproven