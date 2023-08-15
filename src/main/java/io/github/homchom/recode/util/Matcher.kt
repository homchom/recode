package io.github.homchom.recode.util

/**
 * Creates and returns a [MutableMatcher] with [initialPredicates].
 */
fun <T, R : MatchPredicate<T>> matcher(vararg initialPredicates: R) =
    MutableMatcher<T, R>().apply { addAll(initialPredicates) }

/**
 * Creates and returns a [MutableMatcher] with [initialPredicates].
 */
fun <T, R : MatchPredicate<T>> matcher(initialPredicates: Collection<R>) =
    MutableMatcher<T, R>().apply { addAll(initialPredicates) }

/**
 * A functor that matches inputs of type [T] against a specification, returning matches of type [R] or `null`.
 *
 * @see matcher
 */
fun interface Matcher<in T, out R : MatchPredicate<T>> : MatchPredicate<T> {
    fun match(input: T): R?

    override fun matches(input: T) = match(input) != null
}

/**
 * The base interfaces for return types of [Matcher] objects, with the [matches] function for determining whether
 * a match has been made.
 */
fun interface MatchPredicate<in T> {
    infix fun matches(input: T): Boolean
}

/**
 * A simple [List]-backed implementation of [Matcher].
 */
@JvmInline
value class MutableMatcher<T, R : MatchPredicate<T>> private constructor(
    private val predicates: MutableList<R>
) : Matcher<T, R>, MutableList<R> by predicates {
    constructor() : this(mutableListOf())

    override fun match(input: T) = predicates.firstOrNull { it matches input }
}