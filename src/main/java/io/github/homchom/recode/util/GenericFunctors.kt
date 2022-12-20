package io.github.homchom.recode.util

/**
 * A functor that returns the result of [builder], caching the result when the context is null.
 *
 * @param T The context type.
 * @param R The result type.
 */
class NullDefaulted<T : Any, R : Any>(private val builder: (T?) -> R) {
    private var default: R? = null

    operator fun invoke(context: T?) = when {
        context != null -> builder(context)
        default != null -> default!!
        else -> builder(null).also { default = it }
    }
}