package io.github.homchom.recode.util.regex

sealed interface ModifiedRegexElement : RegexElement {
    operator fun plus(modifier: ModifiedRegexElement): ModifiedRegexElement
    operator fun minus(modifier: ModifiedRegexElement): ModifiedRegexElement
}

enum class RegexModifier(private val character: Char) : ModifiedRegexElement {
    IgnoreCase('i'),
    MatchPerLine('m'),
    MatchLineBreaksInAny('s');

    override fun plus(modifier: ModifiedRegexElement): ModifiedRegexElement =
        MutableRegexModifier("$character$modifier")

    override fun minus(modifier: ModifiedRegexElement): ModifiedRegexElement =
        MutableRegexModifier("$character-$modifier")

    override fun toString() = character.toString()
}

private class MutableRegexModifier(private val builder: StringBuilder) : ModifiedRegexElement {
    private val hasNegation get() = '-' in builder

    constructor(string: String) : this(StringBuilder(string))

    override fun plus(modifier: ModifiedRegexElement) = apply {
        if (hasNegation) {
            builder.insert(0, modifier)
        } else {
            builder.append(modifier)
        }
    }

    override fun minus(modifier: ModifiedRegexElement) = apply {
        if (!hasNegation) builder.append('-')
        builder.append(modifier)
    }

    override fun toString() = builder.toString()
}