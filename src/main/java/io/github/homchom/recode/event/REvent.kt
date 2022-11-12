package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ListenableModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.flow.Flow
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

typealias Listener<C, R> = (C, R) -> R

/**
 * Creates a custom [InvokableEvent].
 *
 * @see CustomPhasedEvent
 * @see createFabricEvent
 */
fun <C, R : Any> createEvent(): CustomEvent<C, R> =
    CustomPhasedEvent<_, _, EventPhase>(createFabricEvent(::customListenerOf))

/**
 * Creates a custom [InvokableEvent] with [phases].
 *
 * @see CustomPhasedEvent
 * @see createFabricEventWithPhases
 */
fun <C, R : Any, P : EventPhase> createEventWithPhases(vararg phases: P) =
    CustomPhasedEvent<C, R, P>(createFabricEventWithPhases(phases, ::customListenerOf))

/**
 * Creates a raw array-backed Fabric event of listener type [T] with [factory].
 *
 * @see EventFactory.createArrayBacked
 */
inline fun <reified T> createFabricEvent(noinline factory: (Array<T>) -> T): Event<T> =
    EventFactory.createArrayBacked(T::class.java, factory)

/**
 * Creates a raw Fabric event of listener type [T] with [factory] and with [phases].
 *
 * @see EventFactory.createWithPhases
 */
inline fun <reified T> createFabricEventWithPhases(
    phases: Array<out EventPhase>,
    noinline factory: (Array<T>) -> T
): Event<T> {
    val phaseIDs = phases
        .mapTo(mutableListOf()) { it.id }
        .apply { add(Event.DEFAULT_PHASE) }
        .toTypedArray()
    return EventFactory.createWithPhases(T::class.java, factory, *phaseIDs)
}

/**
 * An event that can be added to via listeners in modules, which are invoked in order when the
 * event is run and if the module is enabled.
 *
 * @param C The event context type (for parameters).
 * @param R The event result type.
 *
 * @see InvokableEvent
 * @see Event
 */
interface REvent<C, R> {
    /**
     * A [Flow] that emits the context of each event invocation. Useful for receiving future
     * invocations as an asynchronous stream.
     */
    val contextFlow: Flow<C>

    @Deprecated("Create and/or listen from a module instead")
    fun register(listener: Listener<C, R>)

    fun listenFrom(module: ListenableModule, listener: Listener<C, R>)
}

/**
 * Listens to this [REvent] without affecting its result.
 */
inline fun <C, R> REvent<C, R>.hookFrom(module: ListenableModule, crossinline hook: (C) -> Unit) =
    listenFrom(module) { context, result ->
        hook(context)
        result
    }

/**
 * An invokable [REvent] that is run when invoked.
 *
 * @param L The raw listener type.
 */
interface InvokableEvent<C, R, L> : REvent<C, R> {
    val fabricEvent: Event<L>

    val invoker: L get() = fabricEvent.invoker()
}

/**
 * An [InvokableEvent] with phases of type [P].
 *
 * @see EventFactory.createWithPhases
 */
interface PhasedEvent<C, R, L, P : EventPhase> : InvokableEvent<C, R, L> {
    fun listenFrom(module: RModule, phase: P, listener: Listener<C, R>)

    fun addPhaseOrdering(first: P, second: P) = fabricEvent.addPhaseOrdering(first.id, second.id)
}

/**
 * A custom [InvokableEvent] that stores its previous result.
 */
interface CustomEvent<C, R : Any> : InvokableEvent<C, R, Listener<C, R>> {
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
 * @see CustomPhasedEvent
 * @see PhasedEvent
 */
interface CustomPhasedEvent<C, R : Any, P : EventPhase> :
    CustomEvent<C, R>, PhasedEvent<C, R, Listener<C, R>, P>

/**
 * A wrapper for a Fabric [Event] phase.
 *
 * @see EventFactory.createWithPhases
 */
interface EventPhase {
    val id: ResourceLocation
}

/**
 * Creates a listener for a [CustomPhasedEvent] by folding on [listeners].
 */
fun <C, R> customListenerOf(listeners: Array<Listener<C, R>>): Listener<C, R> =
    { context, initialValue ->
        listeners.fold(initialValue) { result, listener -> listener(context, result) }
    }