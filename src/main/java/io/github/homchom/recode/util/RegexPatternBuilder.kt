package io.github.homchom.recode.util

import io.github.homchom.recode.logInfo
import io.github.homchom.recode.util.RegexPatternBuilder.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @throws RegexBuilderException
 */
inline fun regex(builder: RegexPatternBuilder.() -> Unit) =
    RegexPatternBuilder().apply(builder).build().also { logInfo("built regex pattern $it") }.let(::Regex)

class RegexPatternBuilder {
    private val subPatterns = mutableListOf<SubPattern>()

    private operator fun SubPattern.unaryPlus(): Quantifiable = also { subPatterns += it }

    fun build() = subPatterns.joinToString("")

    // literal strings

    fun literal(string: String) = +SubPattern(Regex.escape(string), string.length > 1)

    // character classes

    val any get() = +SubPattern(".")

    // quantifiers

    sealed interface Quantifiable {
        fun oneOrMore(): Greedy
        fun zeroOrMore(): Greedy
    }

    sealed interface Greedy {
        fun lazy()
        fun possessive()
    }

    // groups

    fun group(builder: RegexPatternBuilder.() -> Unit): Group {
        val group = SubPattern("(?:")
        subPatterns += group
        group.append(RegexPatternBuilder().apply(builder).build())
        group.append(')')
        return group
    }

    fun atomicGroup(builder: RegexPatternBuilder.() -> Unit) {
        val group = SubPattern("(?>")
        subPatterns += group
        group.append(RegexPatternBuilder().apply(builder).build())
        group.append(')')
    }

    sealed interface Group : Quantifiable, ReadOnlyProperty<Any?, CaptureGroupHandle> {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Group
    }

    class CaptureGroupHandle(val groupName: String)
}

// SubPattern implements all regex typing interfaces; it is up to RegexPatternBuilder members to cast accordingly
private class SubPattern(initial: String, private val needsGrouping: Boolean = false) :
    Quantifiable, Greedy, Group
{
    private val builder = StringBuilder(initial)

    private var quantified = false
    private var grouped = false

    fun append(character: Char) {
        builder.append(character)
    }

    fun append(string: String) {
        builder.append(string)
    }

    override fun toString() = builder.toString()

    // quantifiers

    override fun oneOrMore(): Greedy = quantify('+')
    override fun zeroOrMore(): Greedy = quantify('*')

    override fun lazy() {
        TODO("Not yet implemented")
    }

    override fun possessive() {
        TODO("Not yet implemented")
    }

    private fun quantify(quantifier: Char) = apply {
        if (quantified) throw RegexBuilderException("Sub-pattern already has quantifier '$quantifier'")
        if (needsGrouping) {
            builder.insert(0, "(?:")
            append(')')
        }
        append(quantifier)
        quantified = true
    }

    // groups

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = also {
        if (grouped) throw RegexBuilderException(
            "Sub-pattern already is capturing group with name '${property.name}'"
        )
        builder.replace(2, 3, "<${property.name}>")
        grouped = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        CaptureGroupHandle(property.name)
}

class RegexBuilderException(message: String) : IllegalStateException(message)