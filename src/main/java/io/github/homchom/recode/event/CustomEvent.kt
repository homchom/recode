package io.github.homchom.recode.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

/**
 * Creates a custom [REvent] with a result.
 */
fun <C, R : Any> createEvent() = SimpleCustomEvent<C, R>(createFabricEvent(::eventListenerOf))

/**
 * Creates a custom [REvent] with phases, using the order of the provided enum type as the
 * phase order.
 *
 * @see EventFactory.createWithPhases
 */
inline fun <C, R : Any, reified P : Enum<P>> createEventWithPhases() =
    SimpleCustomEvent(createFabricEventWithPhases<Listener<C, R>, P>(::eventListenerOf))

sealed interface CustomEvent<C, R : Any> : REvent<C, R, Listener<C, R>> {
    /**
     * The result of the previous event invocation, or null if the event has not been run.
     */
    val prevResult: R?

    override fun listen(listener: Listener<C, R>) = delegate.register(listener)

    /**
     * Runs this event, transforming [initialValue] into the event result. [initialValue] may
     * be mutated.
     */
    operator fun invoke(context: C, initialValue: R): R
}

/**
 * @see createEvent
 */
class SimpleCustomEvent<C, R : Any>(
    override val delegate: Event<Listener<C, R>>
) : CustomEvent<C, R> {
    override val prevResult get() = _prevResult
    private var _prevResult: R? = null

    override fun listen(listener: Listener<C, R>) = delegate.register(listener)

    override fun invoke(context: C, initialValue: R) = invoker(context, initialValue)
        .also { _prevResult = it }
}

fun <C, R> eventListenerOf(listeners: Array<Listener<C, R>>): Listener<C, R> =
    { context, initialValue ->
        listeners.fold(initialValue) { result, listener -> listener(context, result) }
    }