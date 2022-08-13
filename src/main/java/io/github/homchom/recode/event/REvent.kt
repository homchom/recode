package io.github.homchom.recode.event

import io.github.homchom.recode.id
import io.github.homchom.recode.init.ModuleHandle
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

typealias Listener<C, R> = (C, R) -> R

/**
 * Creates a raw array-backed Fabric event of listener type [T] with [factory].
 *
 * @see EventFactory.createArrayBacked
 */
inline fun <reified T> createFabricEvent(noinline factory: (Array<T>) -> T): Event<T> =
    EventFactory.createArrayBacked(T::class.java, factory)

/**
 * Creates a raw Fabric event of listener type [T] with [factory] and phases by the ordinals of
 * enum type [P], in order.
 *
 * @see EventFactory.createWithPhases
 */
inline fun <reified T, reified P : Enum<P>> createFabricEventWithPhases(
    noinline factory: (Array<T>) -> T
): Event<T> {
    return EventFactory.createWithPhases(
        T::class.java,
        factory,
        *enumValues<P>().mapTo(mutableListOf()) { id(it.name.lowercase()) }.toTypedArray()
    )
}

/**
 * An event type that can be listened to from within a module. Listeners will only be invoked
 * if the module is enabled.
 */
sealed interface Listenable<C, R> {
    fun listenFrom(module: ModuleHandle, listener: Listener<C, R>)
}

/**
 * A wrapper for a Fabric event. Events can be run (invoked) with context and return results, and
 * are added to via listeners, which are invoked in order when the event is run.
 *
 * @param C The event context type (for parameters).
 * @param R The event result type.
 * @param L The event listener type.
 *
 * @see Event
 */
sealed interface REvent<C, R, L> : Listenable<C, R> {
    val fabricEvent: Event<L>

    val invoker: L get() = fabricEvent.invoker()

    fun addPhaseOrdering(first: ResourceLocation, second: ResourceLocation) =
        fabricEvent.addPhaseOrdering(first, second)
}