package io.github.homchom.recode.event

import io.github.homchom.recode.id
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

inline fun <reified T> createFabricEvent(noinline factory: (Array<T>) -> T): Event<T> =
    EventFactory.createArrayBacked(T::class.java, factory)

inline fun <reified T, reified P : Enum<P>> createFabricEventWithPhases(
    noinline factory: (Array<T>) -> T
): Event<T> {
    return EventFactory.createWithPhases(T::class.java, factory, *createPhases<P>())
}

inline fun <reified P : Enum<P>> createPhases() =
    enumValues<P>().mapTo(mutableListOf()) { id(it.name.lowercase()) }.toTypedArray()

/**
 * @param R The event result. The result type itself is immutable, so it must have at least one
 * mutable property.
 */
fun <T, R> createEvent() = REvent(createFabricEvent<EventListener<T, R>>(::eventListenerOf))

inline fun <T, R, reified P : Enum<P>> createEventWithPhases() =
    REvent(createFabricEventWithPhases<EventListener<T, R>, P>(::eventListenerOf))

/**
 * @see createEvent
 */
fun <T, R> eventListenerOf(listeners: Array<EventListener<T, R>>) = EventListener<T, R> { context ->
    for (listener in listeners) with(listener) { update(context) }
}

fun <T> createHook() = RHook(createFabricEvent<HookListener<T>>(::hookListenerOf))

inline fun <T, reified P : Enum<P>> createHookWithPhases() =
    RHook(createFabricEventWithPhases<HookListener<T>, P>(::hookListenerOf))

/**
 * @see createHook
 */
fun <T> hookListenerOf(listeners: Array<HookListener<T>>) = HookListener<T> { context ->
    for (listener in listeners) listener(context)
}

sealed interface EventWrapper<T> {
    val fabricEvent: Event<T>

    val invoker: T get() = fabricEvent.invoker()

    fun listen(listener: T) = fabricEvent.register(listener)

    fun addPhaseOrdering(first: ResourceLocation, second: ResourceLocation) =
        fabricEvent.addPhaseOrdering(first, second)
}

/**
 * @see createEvent
 */
class REvent<T, R>(
    override val fabricEvent: Event<EventListener<T, R>>
    ) : EventWrapper<EventListener<T, R>> {
    var prevResult: R? = null
        private set

    operator fun invoke(context: T, result: R): R {
        with(invoker) { result.update(context) }
        prevResult = result
        return result
    }
}

// TODO: make value class when kotlin migration is complete
class RHook<T>(override val fabricEvent: Event<HookListener<T>>) : EventWrapper<HookListener<T>> {
    operator fun invoke(context: T) = invoker(context)
}

fun interface EventListener<T, R> {
    fun R.update(context: T)
}

fun interface HookListener<T> {
    operator fun invoke(context: T)
}