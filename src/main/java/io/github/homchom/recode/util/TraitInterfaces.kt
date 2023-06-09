package io.github.homchom.recode.util

/**
 * Subtypes of this interface expect to be hashable as keys of any applicable collection, e.g. [Map] and [Set].
 */
interface KeyHashable {
    override operator fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}

/**
 * A wrapper for a [value] of type [T] that can be unboxed with [invoke].
 */
interface InvokableWrapper<T> {
    val value: T

    /**
     * Unboxes and returns [value].
     */
    operator fun invoke() = value
}