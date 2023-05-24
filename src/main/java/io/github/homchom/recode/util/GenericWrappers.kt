package io.github.homchom.recode.util

/**
 * A wrapper for [T]. Useful in generic contexts of a non-nullable upper bound.
 */
@JvmInline
value class Case<out T>(val content: T) {
    operator fun component1() = content
}

/**
 * @see Case
 */
data class MutableCase<T>(var content: T)

inline fun <T, R> T.encase(block: (T) -> R) = Case(block(this))