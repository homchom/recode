package io.github.homchom.recode.event

import io.github.homchom.recode.init.ModuleView
import net.fabricmc.fabric.api.event.Event

/**
 * Creates a custom [REvent].
 *
 * @see CustomListenable
 * @see createFabricEvent
 */
fun <C, R : Any> createEvent() = customListenable<C, R>(createFabricEvent(::customListenerOf))

/**
 * Creates a custom [REvent] with phases, using the order of the provided enum type as the
 * phase order.
 *
 * @see CustomListenable
 * @see createFabricEventWithPhases
 */
inline fun <C, R : Any, reified P : Enum<P>> createEventWithPhases() =
    customListenable(createFabricEventWithPhases<Listener<C, R>, P>(::customListenerOf))

/**
 * A custom [REvent] that is [ModuleListenable] and stores its previous result.
 */
interface CustomEvent<C, R : Any> : REvent<C, R, Listener<C, R>>, ModuleListenable<C, R> {
    /**
     * The result of the previous event invocation, or null if the event has not been run.
     */
    val prevResult: R?

    override fun listenFrom(module: ModuleView, listener: Listener<C, R>) =
        fabricEvent.register { context, result ->
            if (module.isEnabled) listener(context, result) else result
        }

    /**
     * Runs this event, transforming [initialValue] into the event result. [initialValue] may
     * be mutated.
     */
    operator fun invoke(context: C, initialValue: R): R
}

/**
 * A [CustomEvent] that is [Listenable].
 */
interface CustomListenable<C, R : Any> : CustomEvent<C, R>, Listenable<C, R> {
    override fun listen(listener: Listener<C, R>) = fabricEvent.register(listener)

    override fun listenFrom(module: ModuleView, listener: Listener<C, R>) =
        super<Listenable>.listenFrom(module, listener)
}

/**
 * Constructs a [CustomListenable].
 */
fun <C, R : Any> customListenable(fabricEvent: Event<Listener<C, R>>): CustomListenable<C, R> =
    CustomEventImpl(fabricEvent)

private class CustomEventImpl<C, R : Any>(
    override val fabricEvent: Event<Listener<C, R>>
) : CustomListenable<C, R> {
    override val prevResult get() = _prevResult
    private var _prevResult: R? = null

    override fun invoke(context: C, initialValue: R) = invoker(context, initialValue)
        .also { _prevResult = it }
}

/**
 * Creates a listener for a [CustomListenable] by folding on [listeners].
 */
fun <C, R> customListenerOf(listeners: Array<Listener<C, R>>): Listener<C, R> =
    { context, initialValue ->
        listeners.fold(initialValue) { result, listener -> listener(context, result) }
    }