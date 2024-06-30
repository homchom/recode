package io.github.homchom.recode.util

/**
 * A wrapper for [T]. Useful in generic contexts of a non-nullable upper bound.
 */
data class Case<out T>(val content: T) {
    companion object {
        val ofNull = Case(null)
    }

    /**
     * Unwraps and returns [content].
     */
    operator fun invoke() = content
}

/**
 * @see Case
 */
data class MutableCase<T>(var content: T) {
    /**
     * Unwraps and returns [content].
     */
    operator fun invoke() = content
}