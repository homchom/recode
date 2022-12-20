package io.github.homchom.recode.util

import io.github.homchom.recode.server.Request

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
    fun add(request: Request<T, *, R>) = add(request.matcher)

    override fun match(input: T): MatcherListResult<T, R>? {
        for (matcher in matchers) matcher.match(input)
            ?.let { return MatcherListResult(matcher, it) }
        return null
    }
}

data class MatcherListResult<T, R : Any>(val matcher: Matcher<T, R>, val value: R)

sealed interface Matchable<T> {
    val value: T

    fun <R : Any> matchAgainst(matcher: Matcher<Matchable<T>, R>) = matcher.match(this)
    fun <I : Any, R : Any> matchAgainst(request: Request<Matchable<T>, I, R>, requestInput: I? = null) =
        request.matcher.match(this, requestInput)
}

fun <T, R : Any> Matchable<T>.matchAgainst(matcher: Matcher<T, R>) =
    matchAgainst { matcher.match(it.value) }
fun <T, I : Any, R : Any> Matchable<T>.matchAgainst(request: Request<T, I, R>, requestInput: I? = null) =
    matchAgainst { request.matcher.match(it.value, requestInput) }

class MatchObject<T>(override val value: T) : Matchable<T>

class MatchCache<T>(override val value: T) : Matchable<T> {
    private val map: MutableMap<Matcher<Matchable<T>, Any>, Any> = mutableMapOf()

    override fun <R : Any> matchAgainst(matcher: Matcher<Matchable<T>, R>) =
        matchAgainstCached(matcher) { super.matchAgainst(matcher) }

    override fun <I : Any, R : Any> matchAgainst(request: Request<Matchable<T>, I, R>, requestInput: I?) =
        matchAgainstCached(request.matcher) { super.matchAgainst(request, requestInput) }

    @Suppress("UNCHECKED_CAST")
    private inline fun <R : Any, M : Matcher<Matchable<T>, R>> matchAgainstCached(matcher: M, func: () -> R?) =
        map[matcher] as R? ?: func()?.also { map[matcher] = it }
}