@file:Suppress("UnusedReceiverParameter")

package io.github.homchom.recode.util.regex

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun RegexPatternBuilder.raw(patternString: String, groupIfQuantified: Boolean = false): QuantifiableRegexElement =
    SubPattern(StringBuilder(patternString), groupIfQuantified)

fun RegexPatternBuilder.rawGroup(patternBuilder: RegexPatternBuilder.() -> Unit): CapturableRegexElement {
    val patternString = RegexPatternBuilder().apply(patternBuilder).build()
    return CapturableSubPattern(StringBuilder("(?:$patternString)"))
}

sealed interface RegexElement {
    override fun toString(): String
}

sealed interface QuantifiableRegexElement : RegexElement {
    fun optional(): GreedyRegexElement
    fun oneOrMore(): GreedyRegexElement
    fun zeroOrMore(): GreedyRegexElement

    operator fun times(amount: Int): GreedyRegexElement
    operator fun times(range: IntRange): GreedyRegexElement
    fun atLeast(amount: Int): GreedyRegexElement
}

sealed interface GreedyRegexElement : RegexElement {
    fun lazy()
    fun possessive()
}

sealed interface CapturableRegexElement :
    QuantifiableRegexElement, ReadOnlyProperty<Any?, CapturableRegexElement.Capture>
{
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): CapturableRegexElement

    class Capture(val groupName: String)
}

private class SubPattern(
    private val builder: StringBuilder,
    private val groupIfQuantified: Boolean
) : QuantifiableRegexElement {
    private var quantifier: String? = null

    override fun toString() = builder.toString()

    override fun optional() = quantify("?")
    override fun oneOrMore() = quantify("+")
    override fun zeroOrMore() = quantify("*")

    override fun times(amount: Int) = quantify("{$amount}")
    override fun times(range: IntRange) = quantify("{${range.first},${range.last}}")
    override fun atLeast(amount: Int) = quantify("{$amount,}")

    private fun quantify(quantifier: String): GreedyRegexElement {
        if (this.quantifier != null) throw RegexElementException(
            "RegexConstruct already has quantifier '$quantifier'",
            IllegalStateException()
        )
        if (groupIfQuantified) {
            builder.insert(0, "(?:")
            builder.append(')')
        }
        builder.append(quantifier)
        this.quantifier = quantifier
        return GreedySubPattern(builder)
    }
}

private class GreedySubPattern(private val builder: StringBuilder) : GreedyRegexElement {
    private var customPolicy: String? = null

    override fun toString() = builder.toString()

    override fun lazy() = changePolicy("lazy", '?')
    override fun possessive() = changePolicy("possessive", '+')

    private fun changePolicy(policy: String, quantifier: Char) {
        if (this.customPolicy != null) throw RegexElementException(
            "RegexConstruct cannot be $policy as it is already ${this.customPolicy}",
            IllegalStateException()
        )
        builder.append(quantifier)
        this.customPolicy = policy
    }
}

private class CapturableSubPattern(private val builder: StringBuilder) :
    CapturableRegexElement, QuantifiableRegexElement by SubPattern(builder, false)
{
    private var captured = false

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = also {
        if (captured) throw RegexElementException(
            "RegexConstruct already is capturing group with name '${property.name}'",
            IllegalStateException()
        )
        builder.replace(2, 3, "<${property.name}>")
        captured = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) =
        CapturableRegexElement.Capture(property.name)
}

class RegexElementException(message: String, cause: Throwable?) : IllegalStateException(message, cause)