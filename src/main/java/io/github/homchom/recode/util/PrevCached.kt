package io.github.homchom.recode.util

/**
 * Returns a [PrevCached] that returns results produced by [builder].
 */
fun <T, R : Any> cachePrevious(builder: (T) -> R): PrevCached<T, R> = PrevCachedBuilder(builder)

/**
 * Returns a [PrevCached] that returns results produced by [builder], also caching the result when the
 * context is null.
 */
fun <T : Any, R : Any> cachePreviousAndNull(builder: (T?) -> R): PrevCached<T?, R> = NullCachedBuilder(builder)

/**
 * A functor that returns a result of type [R] given context of type [T], caching the previous result.
 */
sealed interface PrevCached<T, R : Any> {
    operator fun invoke(context: T): R
}

private open class PrevCachedBuilder<T, R : Any>(private val builder: (T) -> R) : PrevCached<T, R> {
    private var prevContext: T? = null
    private var prevResult: R? = null

    override fun invoke(context: T): R {
        val result = if (context == prevContext && prevResult != null) prevResult!! else builder(context)
        prevContext = context
        prevResult = result
        return result
    }
}

private class NullCachedBuilder<T : Any, R : Any>(
    private val builder: (T?) -> R
) : PrevCachedBuilder<T?, R>(builder) {
    private var default: R? = null

    override fun invoke(context: T?) = when {
        context != null -> super.invoke(context)
        default != null -> default!!
        else -> builder(null).also { default = it }
    }
}