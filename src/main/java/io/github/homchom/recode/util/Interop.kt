package io.github.homchom.recode.util

/**
 * A contravariant [java.util.function.Consumer]. Useful for Java interop functions where returning
 * `Unit.INSTANCE` is not desired at call sites.
 */
fun interface InConsumer<in T> {
    fun accept(input: T)
}