package io.github.homchom.recode.util.math

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
    operator fun times(num: Int) = improper(whole * num, numerator * num, denominator)
    operator fun div(num: Int) = improper(0, improperNumerator, denominator * num)

    operator fun plus(other: MixedInt) = improper(
        whole + other.whole,
        numerator * other.denominator + denominator * other.numerator,
        denominator * other.denominator
    )

    operator fun minus(other: MixedInt) = improper(
        whole - other.whole,
        numerator * other.denominator - denominator * other.numerator,
        denominator * other.denominator
    )

    operator fun times(other: MixedInt) = improper(
        0,
        improperNumerator * other.improperNumerator,
        denominator * other.denominator
    )

    operator fun div(other: MixedInt) = improper(
        0,
        improperNumerator * other.denominator,
        denominator * other.improperNumerator
    )

    override fun compareTo(other: MixedInt) =
        improperNumerator * other.denominator - denominator * other.improperNumerator

    operator fun compareTo(other: Int) = improperNumerator - denominator * other

    fun toDouble() = whole + numerator.toDouble() / denominator

    fun toFloat() = whole + numerator.toFloat() / denominator

    companion object {
        /**
         * Constructs a simplified [MixedInt].
         */
        fun simplified(whole: Int, numerator: Int, denominator: Int): MixedInt {
            val gcd = greatestCommonDivisor(numerator, denominator)
            return MixedInt(whole, numerator / gcd, denominator / gcd)
        }

        /**
         * Constructs a [simplified] [MixedInt] with improper components by converting them before construction.
         */
        fun improper(whole: Int, numerator: Int, denominator: Int) = simplified(
            whole + numerator / denominator,
            numerator % denominator,
            denominator
        )
    }
}