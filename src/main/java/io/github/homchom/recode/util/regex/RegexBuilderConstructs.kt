@file:Suppress("UnusedReceiverParameter")

package io.github.homchom.recode.util.regex

import io.github.homchom.recode.util.regex.RegexPattern.Grouping
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@RegexUnproven
fun RegexPatternBuilder.raw(patternCharacter: Char, escape: Boolean = false): RegexCharacterPattern {
    val patternString = patternCharacter.toString()
    val escaped = if (escape) Regex.escape(patternString) else patternString
    return MutableCharacterPattern(StringBuilder(escaped))
}

@RegexUnproven
fun RegexPatternBuilder.raw(
    patternString: String,
    lazyGrouping: Grouping = Grouping.NONE,
    escape: Boolean = false
): QuantifiableRegexPattern {
    val escaped = if (escape) Regex.escape(patternString) else patternString
    return MutablePattern(StringBuilder(escaped), lazyGrouping)
}

@RegexUnproven
fun RegexPatternBuilder.rawRange(start: Char, end: Char): RegexCharacter =
    CharacterRange(start, end)

@RegexUnproven
fun RegexPatternBuilder.rawGroup(patternBuilder: RegexPatternBuilder.() -> Unit): CapturableRegexPattern {
    val patternString = RegexPatternBuilder().apply(patternBuilder).build()
    return MutableCapturablePattern(StringBuilder(patternString))
}

sealed interface RegexConstruct {
    val isCharacterGroupEligible: Boolean

    override fun toString(): String
}

sealed interface RegexPattern : RegexConstruct {
    fun ensureGrouped(isTopLevel: Boolean = false)

    enum class Grouping {
        NONE {
            override fun group(patternBuilder: StringBuilder, isTopLevel: Boolean) = true
        },
        CHARACTER {
            override fun group(patternBuilder: StringBuilder, isTopLevel: Boolean): Boolean {
                patternBuilder.insert(0, '[')
                patternBuilder.append(']')
                return true
            }
        },
        NEGATED_CHARACTER {
            override fun group(patternBuilder: StringBuilder, isTopLevel: Boolean): Boolean {
                patternBuilder.insert(0, "[^")
                patternBuilder.append(']')
                return true
            }
        },
        OPTIONAL {
            override fun group(patternBuilder: StringBuilder, isTopLevel: Boolean): Boolean {
                if (isTopLevel) return false
                patternBuilder.insert(0, "(?:")
                patternBuilder.append(')')
                return true
            }
        };

        abstract fun group(patternBuilder: StringBuilder, isTopLevel: Boolean): Boolean
    }
}

sealed interface QuantifiableRegexPattern : RegexPattern {
    fun optional(): RegexPattern
    fun oneOrMore(): GreedyRegexPattern
    fun zeroOrMore(): GreedyRegexPattern
}

sealed interface RegexCharacterPattern : QuantifiableRegexPattern, RegexCharacter

sealed interface GreedyRegexPattern : RegexPattern {
    fun lazy()
    fun possessive()
}

sealed interface CapturableRegexPattern :
    QuantifiableRegexPattern, ReadOnlyProperty<Any?, CapturableRegexPattern.Capture>
{
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): CapturableRegexPattern

    class Capture(val groupName: String)
}

sealed interface RegexCharacter : RegexConstruct

private class MutablePattern(
    private val builder: StringBuilder,
    private val lazyGrouping: Grouping
) : QuantifiableRegexPattern {
    override val isCharacterGroupEligible get() =
        (lazyGrouping == Grouping.NONE || lazyGrouping == Grouping.CHARACTER) && !grouped

    private var grouped = false
    private var quantified = false

    override fun toString() = builder.toString()

    override fun optional(): RegexPattern {
        quantify('?')
        return this
    }

    override fun oneOrMore(): GreedyRegexPattern {
        quantify('+')
        return MutableGreedyPattern(builder)
    }

    override fun zeroOrMore(): GreedyRegexPattern {
        quantify('*')
        return MutableGreedyPattern(builder)
    }

    override fun ensureGrouped(isTopLevel: Boolean) {
        if (grouped) return
        grouped = lazyGrouping.group(builder, isTopLevel)
    }

    private fun quantify(quantifier: Char) {
        if (quantified) throw RegexConstructException(
            "RegexConstruct already has quantifier '$quantifier'",
            IllegalStateException()
        )
        ensureGrouped()
        builder.append(quantifier)
        quantified = true
    }
}

private class MutableGreedyPattern(private val builder: StringBuilder) : GreedyRegexPattern {
    override val isCharacterGroupEligible get() = false

    override fun toString() = builder.toString()

    override fun ensureGrouped(isTopLevel: Boolean) {}

    override fun lazy() {
        TODO("Not yet implemented")
    }

    override fun possessive() {
        TODO("Not yet implemented")
    }
}

private class MutableCharacterPattern(private val builder: StringBuilder) :
    RegexCharacterPattern, QuantifiableRegexPattern by MutablePattern(builder, Grouping.NONE)

private class MutableCapturablePattern(private val builder: StringBuilder) :
    CapturableRegexPattern, QuantifiableRegexPattern by MutablePattern(builder, Grouping.OPTIONAL)
{
    private var captured = false

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = also {
        ensureGrouped()
        if (captured) throw RegexConstructException(
            "RegexConstruct already is capturing group with name '${property.name}'",
            IllegalStateException()
        )
        builder.replace(2, 3, "<${property.name}>")
        captured = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        CapturableRegexPattern.Capture(property.name)
}

private class CharacterRange(private val start: Char, private val end: Char) : RegexCharacter {
    override val isCharacterGroupEligible get() = true

    override fun toString() = "$start-$end"
}

class RegexConstructException(message: String, cause: Throwable?) : IllegalStateException(message, cause)