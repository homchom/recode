package io.github.homchom.recode.util

import io.github.homchom.recode.event.Trial
import io.github.homchom.recode.event.runTrial

interface Matcher<T, R : Any> {
    suspend fun match(input: T): R?
}

class TrialMatcher<T, R : Any>(private val definition: Trial.(T) -> R) : Matcher<T, R> {
    override suspend fun match(input: T) = runTrial { definition(input) }
}

class MatcherList<T, R : Any>(private val default: (T) -> R) : Matcher<T, R> {
    private val matchers = mutableListOf<Matcher<T, R>>()

    fun add(matcher: Trial.(T) -> R) = matcher.also { matchers += TrialMatcher(it) }

    override suspend fun match(input: T) = matchers.firstNotNullOfOrNull { it.match(input) }
        ?: default(input)
}