package io.github.homchom.recode.event

import net.fabricmc.fabric.api.event.Event

/**
 * Creates a Fabric event with a result.
 *
 * @param T The event context (parameters).
 * @param R The event result. The result type itself is immutable, so it must have at least one
 * mutable property.
 */
fun <T, R> createEvent() = REvent(createFabricEvent<EventListener<T, R>>(::eventListenerOf))

/**
 * @see createEvent
 */
inline fun <T, R, reified P : Enum<P>> createEventWithPhases() =
    REvent(createFabricEventWithPhases<EventListener<T, R>, P>(::eventListenerOf))

fun <T, R> eventListenerOf(listeners: Array<EventListener<T, R>>) = EventListener<T, R> { context ->
    for (listener in listeners) with(listener) { update(context) }
}

/**
 * @see createEvent
 */
class REvent<T, R>(
    override val delegate: Event<EventListener<T, R>>
) : FabricEvent<EventListener<T, R>> {
    var prevResult: R? = null
        private set

    operator fun invoke(context: T, result: R): R {
        with(invoker) { result.update(context) }
        prevResult = result
        return result
    }
}

fun interface EventListener<T, R> {
    fun R.update(context: T)
}