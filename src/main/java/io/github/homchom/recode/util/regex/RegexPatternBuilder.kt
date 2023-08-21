package io.github.homchom.recode.util.regex

/**
 * @throws RegexElementException
 */
inline fun regex(
    useUnixLines: Boolean = false,
    useCanonicalEquivalence: Boolean = false,
    builder: RegexPatternBuilder.() -> Unit
): Regex {
    val pattern = RegexPatternBuilder().apply(builder).build()
    val options = buildSet {
        if (useUnixLines) add(RegexOption.UNIX_LINES)
        if (useCanonicalEquivalence) add(RegexOption.CANON_EQ)
    }
    return Regex(pattern, options)
}

private const val optimizedSpecialChars = """$()*+.?[\^{|"""

// TODO: more regex features as needed (e.g. lookahead and lookbehind)
class RegexPatternBuilder {
    private val elements = mutableListOf<RegexElement>()

    private operator fun <T : RegexElement> T.unaryPlus() = also { elements += it }

    fun build(): String = elements.joinToString("")

    // literal strings

    fun str(str: Any): QuantifiableRegexElement { // parameter named 'str' to prevent IntelliJ label
        val string = str.toString()

        // QE metacharacters cannot be quantified so produce optimized regex in these cases
        val optimizedEscape = if (string.length != 1) null else {
            val char = string[0]
            when {
                char.isLetterOrDigit() || char == '_' || char == ' ' -> string
                char.code >= 128 -> string // regex syntax only uses standard ASCII
                char in optimizedSpecialChars -> "\\$string"
                else -> null
            }
        }

        return +raw(optimizedEscape ?: Regex.escape(string), optimizedEscape == null)
    }

    val space get() = +raw(" ")
    val period get() = +raw("\\.")

    // character classes

    val wordChar get() = +raw("\\w")
    val digit get() = +raw("\\d")
    val newline get() = +raw("\\n")
    val any get() = +raw(".")

    // anchors

    val start get() = +raw("^")
    val end get() = +raw("\$")

    // groups

    fun all(
        modifier: InlineRegexOption? = null,
        builder: RegexPatternBuilder.() -> Unit
    ): QuantifiableRegexElement {
        val patternString = RegexPatternBuilder().apply(builder).build()
        val modifierString = modifier?.toString() ?: ""
        return +raw("(?$modifierString:$patternString)")
    }

    fun any(characterGroup: String) = +raw("[$characterGroup]")

    fun none(characterGroup: String) = +raw("[^$characterGroup]")

    // alternation

    val or get() = +raw("|")

    fun anyStr(vararg branches: Any) = all {
        for (index in branches.indices) {
            str(branches[index])
            if (index != branches.lastIndex) or
        }
    }

    // modifiers

    fun modify(modifier: RegexModifier) = +raw("(?$modifier)")
}