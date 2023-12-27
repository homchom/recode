package io.github.homchom.recode.util.regex

/**
 * Builds and returns a [Regex] pattern.
 *
 * @throws RegexElementException
 *
 * @see RegexPatternBuilder
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

    /**
     * Appends a literal string to the expression.
     */
    fun str(str: String): QuantifiableRegexElement { // parameter named 'str' to prevent IntelliJ label
        // QE metacharacters cannot be quantified so produce optimized regex in these cases
        val optimizedEscape = if (str.length != 1) null else {
            val char = str[0]
            when {
                char.isLetterOrDigit() || char == '_' || char == ' ' -> str
                char.code >= 128 -> str // regex syntax only uses standard ASCII
                char in optimizedSpecialChars -> "\\$str"
                else -> null
            }
        }

        return +raw(optimizedEscape ?: Regex.escape(str), optimizedEscape == null)
    }

    /**
     * Appends a literal character to the expression.
     */
    fun str(char: Char) = str(char.toString())

    /**
     * Appends a space to the expression.
     */
    val space get() = +raw(" ")

    /**
     * Appends a literal period to the expression.
     */
    val period get() = +raw("\\.")

    // character classes


    /**
     * Appends the word character class (`\w`) to the expression.
     */
    val wordChar get() = +raw("\\w")

    /**
     * Appends the digit character class (`\d`) to the expression.
     */
    val digit get() = +raw("\\d")

    /**
     * Appends the newline character class (`\n`) to the expression.
     */
    val newline get() = +raw("\\n")

    /**
     * Appends the "dot" character class (`.`) to the expression.
     */
    val any get() = +raw(".")

    /**
     * Appends the character class denoted by [characterClass] (e.g. `"a-z"` for `[a-z]`) to the expression.
     */
    fun any(characterClass: String) = +raw("[$characterClass]")

    /**
     * Appends the negated character class denoted by [characterClass] (e.g. `a-z` for `[^a-z]`) to the expression.
     */
    fun none(characterClass: String) = +raw("[^$characterClass]")

    // anchors

    /**
     * Appends the start anchor (`^`) to the expression.
     */
    val start get() = +raw("^")

    /**
     * Appends the end anchor (`$`) to the expression.
     */
    val end get() = +raw("\$")

    // groups and alternation

    /**
     * Applies [builder] to this expression builder, grouping the results.
     */
    fun group(
        modifier: InlineRegexOption? = null,
        builder: RegexPatternBuilder.() -> Unit
    ): QuantifiableRegexElement {
        val patternString = RegexPatternBuilder().apply(builder).build()
        val modifierString = modifier?.toString() ?: ""
        return +raw("(?$modifierString:$patternString)")
    }

    /**
     * Appends the alternation delimiter (`|`) to the expression.
     */
    val or get() = +raw("|")

    /**
     * Appends a group to the expression that alternates on [branches]. For example, `anyStr("a", "b")` is
     * shorthand for `group { str("a"); or; str("b"); }`.
     */
    fun anyStr(vararg branches: String) = group {
        for (index in branches.indices) {
            str(branches[index])
            if (index != branches.lastIndex) or
        }
    }

    // modifiers

    /**
     * Applies [modifier] for the remainder of the expression.
     */
    fun modify(modifier: RegexModifier) = +raw("(?$modifier)")
}