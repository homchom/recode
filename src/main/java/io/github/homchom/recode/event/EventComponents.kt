package io.github.homchom.recode.event

import io.github.homchom.recode.init.*
import io.github.homchom.recode.util.Matcher
import kotlinx.coroutines.launch

/**
 * A [CustomEvent] without a result.
 *
 * @see hookFrom
 */
interface HookEvent<C> : CustomEvent<C, Unit> {
    fun run(context: C) = run(context, Unit)
}

/**
 * An [REvent] with a boolean result; this should be used for events whose listeners "validate"
 * it and determine whether the action that caused it should proceed.
 */
interface ValidatedEvent<C> : REvent<C, Boolean>

/**
 * A [CustomEvent] with children. When listened to by a module, the children will be implicitly added.
 */
class DependentEvent<C, R : Any>(
    private val delegate: CustomEvent<C, R>,
    vararg children: RModule
) : CustomEvent<C, R> by delegate {
    private val children = children.clone()

    @MutatesModuleState
    override fun listenFrom(module: ListenableModule, listener: Listener<C, R>) {
        for (child in children) child.addParent(module)
        delegate.listenFrom(module, listener)
    }
}

interface MatchedEvent<T, C, R : Any> : CustomEvent<C, R> {
    fun matchAndRun(input: T, initialValue: R, withResult: (R) -> Unit)
}

class MatcherCallbackEvent<T, C, R : Any> private constructor(
    private val matcher: Matcher<T, C>,
    private val module: CoroutineModule,
    eventDelegate: CustomEvent<C, R>
) : MatchedEvent<T, C, R>, CustomEvent<C, R> by eventDelegate, RModule by module {
    constructor(matcher: Matcher<T, C>, eventDelegate: CustomEvent<C, R> = createEvent()) :
            this(matcher, module(), eventDelegate)

    override fun matchAndRun(input: T, initialValue: R, withResult: (R) -> Unit) {
        module.coroutineScope.launch {
            run(matcher.match(input), initialValue).let(withResult)
        }
    }
}