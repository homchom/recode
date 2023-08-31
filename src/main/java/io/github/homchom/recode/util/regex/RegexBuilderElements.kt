@file:Suppress("UnusedReceiverParameter")

package io.github.homchom.recode.util.regex

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun RegexPatternBuilder.raw(patternString: String, groupIfQuantified: Boolean = false): QuantifiableRegexElement =
    SubPattern(StringBuilder(patternString), groupIfQuantified)

sealed interface RegexElement : ReadOnlyProperty<Any?, RegexElement.Capture> {
    override fun toString(): String

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): RegexElement

    class Capture(val groupName: String)
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
    fun lazy(): RegexElement
    fun possessive(): RegexElement
}

private class SubPattern(
    private val builder: StringBuilder,
    private val groupIfQuantified: Boolean
) : QuantifiableRegexElement {
    private var quantifier: String? = null
    private var captured = false

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
        return Greedy()
    }

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = also {
        if (captured) throw RegexElementException(
            "RegexConstruct already is capturing group with name '${property.name}'",
            IllegalStateException()
        )
        if (builder.startsWith("(?:")) {
            builder.replace(2, 3, "<${property.name}>")
        } else {
            builder.insert(0, "(?<${property.name}>")
            builder.append(')')
        }
        captured = true
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = RegexElement.Capture(property.name)

    private inner class Greedy : GreedyRegexElement, RegexElement by this {
        private var customPolicy: String? = null

        override fun lazy() = changePolicy("lazy", '?')
        override fun possessive() = changePolicy("possessive", '+')

        private fun changePolicy(policy: String, quantifier: Char) = apply {
            if (this.customPolicy != null) throw RegexElementException(
                "RegexConstruct cannot be $policy as it is already ${this.customPolicy}",
                IllegalStateException()
            )
            builder.append(quantifier)
            this.customPolicy = policy
        }
    }
}

class RegexElementException(message: String, cause: Throwable?) : IllegalStateException(message, cause)