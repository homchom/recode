package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ListenableModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import net.fabricmc.fabric.api.event.Event

/**
 * Wraps an existing event into an [InvokableEvent], using [transform] to map recode listeners to its
 * specification.
 */
fun <C, R, L> wrapEvent(event: Event<L>, transform: (Listener<C, R>) -> L): InvokableEvent<C, R, L> =
    wrapEventWithPhases<_, _, _, EventPhase>(event, transform)

/**
 * @see wrapEvent
 * @see createEventWithPhases
 */
fun <C, R, L, P : EventPhase> wrapEventWithPhases(
    event: Event<L>,
    transform: (Listener<C, R>) -> L
): PhasedEvent<C, R, L, P> {
    return EventWrapper(event, transform)
}

private open class EventWrapper<C, R, L, P : EventPhase>(
    override val fabricEvent: Event<L>,
    private val transform: (Listener<C, R>) -> L
) : PhasedEvent<C, R, L, P> {
    override val contextFlow: Flow<C> by lazy {
        MutableSharedFlow<C>().also { flow ->
            transformAndRegister { context, result ->
                runBlocking { flow.emit(context) }
                result
            }
        }
    }

    @Deprecated("Use listenFrom")
    override fun register(listener: Listener<C, R>) = transformAndRegister(listener)

    private fun transformAndRegister(listener: Listener<C, R>) =
        fabricEvent.register(transform(listener))

    override fun listenFrom(module: ListenableModule, listener: Listener<C, R>) =
        fabricEvent.register(transformFrom(module, listener))

    override fun listenFrom(module: RModule, phase: P, listener: Listener<C, R>) =
        fabricEvent.register(phase.id, transformFrom(module, listener))

    private inline fun transformFrom(module: RModule, crossinline listener: Listener<C, R>) =
        transform { context, result ->
            if (module.isEnabled) listener(context, result) else result
        }
}

/**
 * Constructs a [CustomPhasedEvent].
 */
@Suppress("FunctionName")
fun <C, R : Any, P : EventPhase> CustomPhasedEvent(
    fabricEvent: Event<Listener<C, R>>
): CustomPhasedEvent<C, R, P> {
    return CustomEventWrapper(fabricEvent)
}

private class CustomEventWrapper<C, R : Any, P : EventPhase>(
    fabricEvent: Event<Listener<C, R>>
) : CustomPhasedEvent<C, R, P>, EventWrapper<C, R, Listener<C, R>, P>(fabricEvent, { it }) {
    override val prevResult get() = _prevResult
    private var _prevResult: R? = null

    override fun run(context: C, initialValue: R) = invoker(context, initialValue)
        .also { _prevResult = it }
}