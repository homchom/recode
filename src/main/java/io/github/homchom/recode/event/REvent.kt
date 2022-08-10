package io.github.homchom.recode.event

import io.github.homchom.recode.id
import io.github.homchom.recode.init.ModuleView
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
 * Wraps an existing event into an [REvent], using [transform] to map recode listeners to its
 * specification.
 */
fun <C, R, L> wrapEvent(event: Event<L>, transform: (Listener<C, R>) -> L): WrappedEvent<C, R, L> =
    WrappedEventImpl(event, transform)

/**
 * An event type that can be listened to from within a module. Listeners will only be invoked
 * if the module is enabled.
 */
sealed interface ModuleListenable<C, R> {
    fun listenFrom(module: ModuleView, listener: Listener<C, R>)
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
sealed interface REvent<C, R, L> : ModuleListenable<C, R> {
    val fabricEvent: Event<L>

    val invoker: L get() = fabricEvent.invoker()

    fun addPhaseOrdering(first: ResourceLocation, second: ResourceLocation) =
        fabricEvent.addPhaseOrdering(first, second)
}

/**
 * An [ModuleListenable] that can also be listened to standalone.
 */
sealed interface Listenable<C, R> : ModuleListenable<C, R> {
    fun listen(listener: Listener<C, R>)

    override fun listenFrom(module: ModuleView, listener: Listener<C, R>) =
        listen { context, result ->
            if (module.isEnabled) listener(context, result) else result
        }
}

/**
 * @see wrapEvent
 */
interface WrappedEvent<C, R, L> : REvent<C, R, L>, Listenable<C, R>

private class WrappedEventImpl<C, R, L>(
    override val fabricEvent: Event<L>,
    private val transform: (Listener<C, R>) -> L
) : WrappedEvent<C, R, L> {
    override fun listen(listener: Listener<C, R>) = fabricEvent.register(transform(listener))
}