package io.github.homchom.recode.util

/**
 * A general-purpose function object that matches inputs of type [T] against a specification, returning matches
 * of type [R] or `null`.
 *
 * @see matcherOf
 */
fun interface Matcher<in T, out R : Any> {
    fun match(input: T): R?

    infix fun matches(input: T) = match(input) != null
}

/**
 * Creates a [MatcherList] with [initialPredicates].
 */
fun <T, R : Any> matcherOf(vararg initialPredicates: Matcher<T, R>) =
    MatcherList<T, R>().apply { addAll(initialPredicates) }

/**
 * Creates a [MatcherList] with [initialPredicates].
 */
fun <T, R : Matcher<T, R>> matcherOf(initialPredicates: Collection<R>) =
    MatcherList<T, R>().apply { addAll(initialPredicates) }

/**
 * A [List]-backed implementation of [Matcher] that yields the first successful match among its elements.
 */
@JvmInline
value class MatcherList<T, R : Any> private constructor(
    private val elements: MutableList<Matcher<T, R>>
) : Matcher<T, R>, MutableList<Matcher<T, R>> by elements {
    constructor() : this(mutableListOf())

    override fun match(input: T) = firstNotNullOfOrNull {
        try {
            it.match(input)
        } catch (npe: NullPointerException) {
            npe.printStackTrace()
            throw npe
        }
    }
}