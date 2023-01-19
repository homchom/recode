package io.github.homchom.recode.util

// TODO: make these value classes when supported

/**
 * A wrapper for [T]. Useful in generic contexts of a non-nullable upper bound.
 */
data class Case<out T>(val content: T)

/**
 * @see Case
 */
data class MutableCase<T>(var content: T)

inline fun <T, R> T.encase(block: (T) -> R) = Case(block(this))