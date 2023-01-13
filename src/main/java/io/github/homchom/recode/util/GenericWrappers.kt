package io.github.homchom.recode.util

// TODO: make these value classes when supported

/**
 * A wrapper for [T]?. Useful in generic contexts of a non-nullable upper bound.
 */
class Case<out T : Any>(val content: T? = null) {
    operator fun invoke() = content
}

/**
 * @see Case
 */
class MutableCase<T : Any>(var content: T? = null) {
    operator fun invoke() = content
}

inline fun <T, R : Any> T.encase(block: (T) -> R) = Case(block(this))