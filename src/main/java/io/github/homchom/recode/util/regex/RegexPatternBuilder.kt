package io.github.homchom.recode.util.regex

import io.github.homchom.recode.logInfo

/**
 * @throws RegexElementException
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
    private val elements = mutableListOf<RegexElement>()

    private operator fun <T : RegexElement> T.unaryPlus() = also { elements += it }

    fun build(): String = elements.joinToString("")

    // literal strings

    fun str(string: String) = +raw(Regex.escape(string), true)

    // character classes

    fun wordChar() = +raw("\\w")
    fun digit() = +raw("\\d")
    fun any() = +raw(".")

    // groups

    fun group(builder: RegexPatternBuilder.() -> Unit) = +rawGroup(builder)

    fun group(modifier: ModifiedRegexElement, builder: RegexPatternBuilder.() -> Unit): QuantifiableRegexElement {
        val patternString = RegexPatternBuilder().apply(builder).build()
        return +raw("(?$modifier:$patternString)")
    }

    fun any(characterGroup: String) = +raw("[$characterGroup]")

    fun none(characterGroup: String) = +raw("[^$characterGroup]")

    // alternation

    fun or() = +raw("|")

    // modifiers

    fun modify(modifier: RegexModifier) = +raw("(?$modifier)")
}

@RequiresOptIn("This regex API has not been laboriously tested for potential edge issues. If this is " +
        "used and something breaks, double check here for API bugs")
annotation class RegexUnproven