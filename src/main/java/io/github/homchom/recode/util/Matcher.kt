package io.github.homchom.recode.util

import io.github.homchom.recode.event.Trial
import io.github.homchom.recode.event.runTrial

sealed interface Matcher<T, R : Any> {
    fun match(input: T): R?
}

class TrialMatcher<T, R : Any>(private val definition: Trial.(T) -> R) : Matcher<T, R> {
    override fun match(input: T) = runTrial { definition(input) }
}

abstract class TrialMatcherList<T, R : Any> private constructor(
    private val matchers: MutableList<Matcher<T, R>>
) : Matcher<T, R>, List<Matcher<T, R>> by matchers {
    constructor() : this(mutableListOf())

    protected fun add(matcher: Trial.(T) -> R) = matcher.also { matchers += TrialMatcher(it) }

    protected abstract fun default(input: T): R

    override fun match(input: T) = matchers.firstNotNullOfOrNull { it.match(input) }
        ?: default(input)
}