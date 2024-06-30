package io.github.homchom.recode.util.math

/**
 * Returns the square of this Int.
 */
fun Int.squared() = this * this

/**
 * Returns the square of this Long.
 */
fun Long.squared() = this * this

/**
 * Returns the square of this Float.
 */
fun Float.squared() = this * this

/**
 * Returns the square of this Double.
 */
fun Double.squared() = this * this

/**
 * @return the [floor modulo](https://en.wikipedia.org/wiki/Modulo#Variants_of_the_definition)
 * of this Int and [other].
 */
infix fun Int.mod(other: Int) = Math.floorMod(this, other)

/**
 * @return the [floor modulo](https://en.wikipedia.org/wiki/Modulo#Variants_of_the_definition)
 * of this Long and [other].
 */
infix fun Long.mod(other: Long) = Math.floorMod(this, other)

/**
 * @return the greatest common divisor of [a] and [b] using the
 * [Euclidean algorithm](https://en.wikipedia.org/wiki/Euclidean_algorithm).
 */
tailrec fun greatestCommonDivisor(a: Int, b: Int): Int =
    if (b == 0) a else greatestCommonDivisor(b, a % b)