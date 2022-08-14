package io.github.homchom.recode.event

import io.github.homchom.recode.id
import io.github.homchom.recode.init.ModuleHandle
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

typealias Listener<C, R> = (C, R) -> R

/**
 * Creates a custom [REvent].
 *
 * @see CustomEvent
 * @see createFabricEvent
 */
fun <C, R : Any> createEvent() = CustomEvent<C, R>(createFabricEvent(::customListenerOf))

/**
 * Creates a custom [REvent] with phases, using the order of the provided enum type as the
 * phase order.
 *
 * @see CustomEvent
 * @see createFabricEventWithPhases
 */
inline fun <C, R : Any, reified P : Enum<P>> createEventWithPhases() =
    CustomEvent(createFabricEventWithPhases<Listener<C, R>, P>(::customListenerOf))

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
    val phases = enumValues<P>()
        .mapTo(mutableListOf()) { id(it.name.lowercase()) }
        .apply { add(Event.DEFAULT_PHASE) }
        .toTypedArray()
    return EventFactory.createWithPhases(T::class.java, factory, *phases)
}

/**
 * An event type that can be listened to from within a module.
 *
 * @see REvent
 */
sealed interface Listenable<C, R> {
    @Deprecated("Create and/or listen from a module instead")
    fun listen(listener: Listener<C, R>)

    /**
     * Listens to [listener] from [module].
     *
     * @param explicit If true, the module will be recorded as containing a listener of this type.
     *
     * @throws IllegalStateException if [explicit] and an explicit listener has already been added
     * from [module].
     */
    fun listenFrom(module: ModuleHandle, explicit: Boolean = true, listener: Listener<C, R>)
}

/**
 * A wrapper for a Fabric event. Events can be run (invoked) with context and return results, and
 * are added to via listeners in modules, which are invoked in order when the event is run and if
 * the module is enabled.
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

/**
 * A custom [REvent] that stores its previous result.
 */
interface CustomEvent<C, R : Any> : REvent<C, R, Listener<C, R>> {
    /**
     * The result of the previous event invocation, or null if the event has not been run.
     */
    val prevResult: R?

    /**
     * Runs this event, transforming [initialValue] into the event result. [initialValue] may
     * be mutated.
     */
    operator fun invoke(context: C, initialValue: R): R
}

/**
 * Creates a listener for a [CustomEvent] by folding on [listeners].
 */
fun <C, R> customListenerOf(listeners: Array<Listener<C, R>>): Listener<C, R> =
    { context, initialValue ->
        listeners.fold(initialValue) { result, listener -> listener(context, result) }
    }