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

sealed interface FabricEvent<T> {
    val delegate: Event<T>

    val invoker: T get() = delegate.invoker()

    fun listen(listener: T) = delegate.register(listener)

    fun addPhaseOrdering(first: ResourceLocation, second: ResourceLocation) =
        delegate.addPhaseOrdering(first, second)
}