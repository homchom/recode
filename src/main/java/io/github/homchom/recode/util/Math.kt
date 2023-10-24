package io.github.homchom.recode.util

/**
 * Returns the [floor modulo](https://en.wikipedia.org/wiki/Modulo#Variants_of_the_definition)
 * of this Int and [other].
 */
infix fun Int.mod(other: Int) = Math.floorMod(this, other)

/**
 * Returns the [floor modulo](https://en.wikipedia.org/wiki/Modulo#Variants_of_the_definition)
 * of this Long and [other].
 */
infix fun Long.mod(other: Long) = Math.floorMod(this, other)

/**
 * Returns the greatest common factor of [a] and [b] using the
 * [Euclidean algorithm](https://en.wikipedia.org/wiki/Euclidean_algorithm).
 */
tailrec fun greatestCommonFactor(a: Int, b: Int): Int =
    if (b == 0) a else greatestCommonFactor(b, a % b)

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
         * Constructs a [MixedInt] with improper components by converting them before construction.
         */
        fun improper(whole: Int, numerator: Int, denominator: Int): MixedInt {
            val gcf = greatestCommonFactor(whole, denominator)
            return MixedInt(
                whole + numerator / denominator,
                (numerator % denominator) / gcf,
                denominator / gcf
            )
        }
    }
}