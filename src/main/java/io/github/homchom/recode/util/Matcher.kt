package io.github.homchom.recode.util

fun interface Matcher<in T, out R : Any> {
    fun match(input: T): R?
}

inline fun <reified E, T, R : Any> enumMatcher(): GroupMatcher<T, R>
    where E : Enum<E>, E : Matcher<T, R>
{
    return MatcherList(enumValues<E>().asList())
}

interface GroupMatcher<T, R : Any> : Matcher<T, MatcherListResult<T, R>>

class MatcherList<T, R : Any>(matchers: List<Matcher<T, R>>) : GroupMatcher<T, R>, List<Matcher<T, R>> by matchers {
    private val matchers = matchers.toMutableList()

    constructor() : this(mutableListOf())

    fun add(matcher: Matcher<T, R>) = matcher.also { matchers += it }

    override fun match(input: T): MatcherListResult<T, R>? {
        for (matcher in matchers) matcher.match(input)
            ?.let { return MatcherListResult(matcher, it) }
        return null
    }
}

data class MatcherListResult<T, R : Any>(val matcher: Matcher<T, R>, val value: R)