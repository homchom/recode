package io.github.homchom.recode.util

// TODO: revisit

fun interface Matcher<in T, out R : Any> {
    fun match(input: T): R?
}

inline fun <reified E, T, R : Any> enumMatcher(): GroupMatcher<T, R, E>
    where E : Enum<E>, E : Matcher<T, R>
{
    return MatcherList(enumValues<E>().asList())
}

interface GroupMatcher<T, R : Any, M : Matcher<T, R>> : Matcher<T, GroupMatcherResult<T, R, M>>

class MatcherList<T, R : Any, M : Matcher<T, R>>(matchers: List<M>) : GroupMatcher<T, R, M>, List<M> by matchers {
    private val matchers = matchers.toMutableList()

    constructor() : this(mutableListOf())

    fun add(matcher: M) = matcher.also { matchers += it }

    override fun match(input: T): GroupMatcherResult<T, R, M>? {
        for (matcher in matchers) matcher.match(input)
            ?.let { return GroupMatcherResult(matcher, it) }
        return null
    }
}

data class GroupMatcherResult<T, R : Any, M : Matcher<T, R>>(val matcher: M, val value: R)