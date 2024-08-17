package io.github.homchom.recode.util.math

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
 * @return the greatest common factor of [a] and [b] using the
 * [Euclidean algorithm](https://en.wikipedia.org/wiki/Euclidean_algorithm).
 */
tailrec fun greatestCommonFactor(a: Int, b: Int): Int =
    if (b == 0) a else greatestCommonFactor(b, a % b)