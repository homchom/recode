package io.github.homchom.recode.util

fun interface Matcher<T, R> {
    fun match(input: T): R
}

class MatcherList<T, R : Any> private constructor(
    private val matchers: MutableList<Matcher<T, R?>>,
    private val default: (T) -> R
) : Matcher<T, R>, List<Matcher<T, R?>> by matchers {
    constructor(default: (T) -> R) : this(mutableListOf(), default)

    fun add(matcher: NullableScope.(T) -> R) = Matcher { input: T ->
        nullable { matcher(input) }
    }.also { matchers += it }

    override fun match(input: T) = matchers.firstNotNullOfOrNull { it.match(input) }
        ?: default(input)
}