@file:Suppress("UnusedReceiverParameter")

package io.github.homchom.recode.util.regex

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal fun RegexPatternBuilder.raw(
    patternString: String,
    groupIfQuantified: Boolean = false
): QuantifiableRegexElement {
    return SubPattern(StringBuilder(patternString), groupIfQuantified)
}

/**
 * An abstract representation of a [Regex] token. Any RegexElement can be implicitly placed in a capture group
 * via [provideDelegate].
 */
sealed interface RegexElement : ReadOnlyProperty<Any?, RegexElement.Capture> {
    override fun toString(): String

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): RegexElement

    class Capture(val groupName: String)
}

/**
 * A quantifiable [RegexElement]. For example, QuantifiableRegexElements can be made [optional].
 */
sealed interface QuantifiableRegexElement : RegexElement {
    /**
     * Applies the optional quantifier (`?`) to this element,
     * returning a [GreedyRegexElement].
     */
    fun optional(): GreedyRegexElement

    /**
     * Applies the plus quantifier (`+`) to this element,
     * returning a [GreedyRegexElement].
     */
    fun oneOrMore(): GreedyRegexElement

    /**
     * Applies the star quantifier (`*`) to this element,
     * returning a [GreedyRegexElement].
     */
    fun zeroOrMore(): GreedyRegexElement

    /**
     * Applies a numeric quantifier (e.g. `{3}`) to this element,
     * returning a [GreedyRegexElement].
     */
    operator fun times(amount: Int): GreedyRegexElement

    /**
     * Applies a numeric range quantifier (e.g. `{3,5}`) to this element,
     * returning a [GreedyRegexElement].
     */
    operator fun times(range: IntRange): GreedyRegexElement

    /**
     * Applies an open-ended numeric range quantifier (e.g. `{3,}`) to this element,
     * returning a [GreedyRegexElement].
     */
    fun atLeast(amount: Int): GreedyRegexElement
}

/**
 * A greedy [RegexElement] that can be made [lazy] or [possessive].
 */
sealed interface GreedyRegexElement : RegexElement {
    /**
     * Makes the preceding quantifier lazy by appending `?`.
     */
    fun lazy(): RegexElement

    /**
     * Makes the preceding quantifier possessive by appending `+`.
     */
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