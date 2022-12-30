package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.HookableModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.*
import net.fabricmc.fabric.api.event.Event

/**
 * Wraps an existing Fabric [Event] into a [Hook], using [transform] to map recode listeners to its
 * specification.
 */
fun <T, R, H> wrapFabricEvent(event: Event<H>, transform: (HookListener<T, R>) -> H): WrappedHook<T, R, H> =
    wrapFabricEventWithPhases<_, _, _, EventPhase>(event, transform)

/**
 * @see wrapFabricEvent
 * @see createHookWithPhases
 */
fun <T, R, H, P : EventPhase> wrapFabricEventWithPhases(
    event: Event<H>,
    transform: (HookListener<T, R>) -> H
): WrappedPhasedHook<T, R, H, P> {
    return EventWrapper(event, transform)
}

private open class EventWrapper<T, R, H, P : EventPhase>(
    override val fabricEvent: Event<H>,
    private val transform: (HookListener<T, R>) -> H
) : WrappedPhasedHook<T, R, H, P> {
    private val async = createEvent<T>()

    override val notifications get() = async.notifications

    @Deprecated("Use hookFrom")
    override fun register(listener: HookListener<T, R>) = transformAndRegister(listener)

    private fun transformAndRegister(listener: HookListener<T, R>) =
        fabricEvent.register(transform(listener))

    override fun hookFrom(module: HookableModule, listener: HookListener<T, R>) =
        fabricEvent.register(transformFrom(module, listener))

    override fun hookFrom(module: HookableModule, phase: P, listener: HookListener<T, R>) =
        fabricEvent.register(phase.id, transformFrom(module, listener))

    private inline fun transformFrom(module: RModule, crossinline listener: HookListener<T, R>) =
        transform { context, result ->
            if (module.isEnabled) listener(context, result) else result
        }
}

/**
 * Constructs a [CustomPhasedHook].
 */
@Suppress("FunctionName")
fun <T, R : Any, P : EventPhase> CustomPhasedHookable(
    fabricEvent: Event<HookListener<T, R>>
): CustomPhasedHook<T, R, P> {
    return object : CustomPhasedHook<T, R, P>, EventWrapper<T, R, HookListener<T, R>, P>(fabricEvent, { it }) {
        override val prevResult get() = _prevResult

        @Suppress("ObjectPropertyName")
        private var _prevResult: R? = null

        override fun run(context: T, initialValue: R) = invoker(context, initialValue)
            .also { _prevResult = it }
    }
}