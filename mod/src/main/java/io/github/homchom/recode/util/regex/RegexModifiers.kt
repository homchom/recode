package io.github.homchom.recode.util.regex

sealed interface InlineRegexOption {
    operator fun plus(modifier: InlineRegexOption): InlineRegexOption
    operator fun minus(modifier: InlineRegexOption): InlineRegexOption
}

enum class RegexModifier(private val character: Char) : InlineRegexOption {
    IgnoreCase('i'),
    MatchPerLine('m'),
    MatchLineBreaksInAny('s');

    override fun plus(modifier: InlineRegexOption): InlineRegexOption =
        MutableRegexModifier("$character$modifier")

    override fun minus(modifier: InlineRegexOption): InlineRegexOption =
        MutableRegexModifier("$character-$modifier")

    override fun toString() = character.toString()
}

private class MutableRegexModifier(private val builder: StringBuilder) : InlineRegexOption {
    private val hasNegation get() = '-' in builder

    constructor(string: String) : this(StringBuilder(string))

    override fun plus(modifier: InlineRegexOption) = apply {
        if (hasNegation) {
            builder.insert(0, modifier)
        } else {
            builder.append(modifier)
        }
    }

    override fun minus(modifier: InlineRegexOption) = apply {
        if (!hasNegation) builder.append('-')
        builder.append(modifier)
    }

    override fun toString() = builder.toString()
}