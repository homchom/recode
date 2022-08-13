package io.github.homchom.recode.event

import io.github.homchom.recode.init.ModuleHandle
import net.fabricmc.fabric.api.event.Event

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
 * Wraps an existing event into an [REvent], using [transform] to map recode listeners to its
 * specification.
 */
fun <C, R, L> wrapEvent(event: Event<L>, transform: (Listener<C, R>) -> L): REvent<C, R, L> =
    EventWrapper(event, transform)

private open class EventWrapper<C, R, L>(
    override val fabricEvent: Event<L>,
    private val transform: (Listener<C, R>) -> L
) : REvent<C, R, L> {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun listen(listener: Listener<C, R>) = fabricEvent.register(transform(listener))

    override fun listenFrom(module: ModuleHandle, listener: Listener<C, R>) =
        fabricEvent.register(transform { context, result ->
            if (module.isEnabled) listener(context, result) else result
        })
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
 * Constructs a [CustomEvent].
 */
@Suppress("FunctionName")
fun <C, R : Any> CustomEvent(fabricEvent: Event<Listener<C, R>>): CustomEvent<C, R> {
    return CustomEventWrapper(fabricEvent)
}

private class CustomEventWrapper<C, R : Any>(
    fabricEvent: Event<Listener<C, R>>
) : CustomEvent<C, R>, EventWrapper<C, R, Listener<C, R>>(fabricEvent, { it }) {
    override val prevResult get() = _prevResult
    private var _prevResult: R? = null

    override fun invoke(context: C, initialValue: R) = invoker(context, initialValue)
        .also { _prevResult = it }
}

/**
 * Creates a listener for a [CustomEvent] by folding on [listeners].
 */
fun <C, R> customListenerOf(listeners: Array<Listener<C, R>>): Listener<C, R> =
    { context, initialValue ->
        listeners.fold(initialValue) { result, listener -> listener(context, result) }
    }