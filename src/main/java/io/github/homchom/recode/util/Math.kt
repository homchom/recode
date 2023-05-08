package io.github.homchom.recode.util

import java.util.concurrent.atomic.AtomicReference

typealias AtomicMixedInt = AtomicReference<MixedInt>

/**
 * Returns the greatest common factor of [a] and [b] using the
 * [Euclidean algorithm](https://en.wikipedia.org/wiki/Euclidean_algorithm).
 */
tailrec fun greatestCommonFactor(a: Int, b: Int): Int = when {
    a == 0 -> b
    b == 0 -> a
    else -> greatestCommonFactor(b, a % b)
}

/**
 * Constructs a [MixedInt] with improper components by converting them before construction.
 */
fun improperMixedInt(whole: Int, numerator: Int, denominator: Int): MixedInt {
    val gcf = greatestCommonFactor(whole, denominator)
    return MixedInt(
        whole + numerator / denominator,
        (numerator % denominator) / gcf,
        denominator / gcf
    )
}

@Suppress("FunctionName")
fun AtomicMixedInt(whole: Int, numerator: Int = 0, denominator: Int = 1) =
    AtomicReference(MixedInt(whole, numerator, denominator))

/**
 * A [mixed number](https://en.wikipeedia.org/wiki/Fraction#Forms_of_fractions) with [Int] components.
 *
 * @param denominator must be positive and greater than or equal to [numerator].
 */
data class MixedInt(val whole: Int, val numerator: Int = 0, val denominator: Int = 1) : Comparable<MixedInt> {
    val improperNumerator get() = whole * denominator + numerator

    init {
        require(denominator > 0) { "The denominator of a mixed number must be positive" }
        require(numerator < denominator) {
            "The numerator of a mixed number must be less than the denominator"
        }
    }

    operator fun plus(num: Int) = copy(whole = whole + num)
    operator fun minus(num: Int) = copy(whole = whole - num)
    operator fun times(num: Int) = improperMixedInt(whole * num, numerator * num, denominator)
    operator fun div(num: Int) = improperMixedInt(0, improperNumerator, denominator * num)

    operator fun plus(other: MixedInt) = improperMixedInt(
        whole + other.whole,
        numerator * other.denominator + denominator * other.numerator,
        denominator * other.denominator
    )

    operator fun minus(other: MixedInt) = improperMixedInt(
        whole - other.whole,
        numerator * other.denominator - denominator * other.numerator,
        denominator * other.denominator
    )

    operator fun times(other: MixedInt) = improperMixedInt(
        0,
        improperNumerator * other.improperNumerator,
        denominator * other.denominator
    )

    operator fun div(other: MixedInt) = improperMixedInt(
        0,
        improperNumerator * other.denominator,
        denominator * other.improperNumerator
    )

    override fun compareTo(other: MixedInt) =
        improperNumerator * other.denominator - denominator * other.improperNumerator

    operator fun compareTo(other: Int) = improperNumerator - denominator * other

    fun toDouble() = whole + numerator.toDouble() / denominator

    fun toFloat() = whole + numerator.toFloat() / denominator
}