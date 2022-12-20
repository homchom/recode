package io.github.homchom.recode.util

/**
 * A wrapper for [T]?. Useful in generic contexts of a non-nullable upper bound.
 */
class Case<T : Any>(val content: T? = null) {
    operator fun invoke() = content
}

/**
 * @see Case
 */
class MutableCase<T : Any>(var content: T? = null) {
    operator fun invoke() = content
}