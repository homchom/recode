package io.github.homchom.recode.util

/**
 * A wrapper for a [value] of type [T] that can be unboxed with [invoke].
 */
interface InvokableWrapper<out T> {
    val value: T

    /**
     * Unboxes and returns [value].
     */
    operator fun invoke() = value
}