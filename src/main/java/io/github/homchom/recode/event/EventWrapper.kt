package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.HookableModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import net.fabricmc.fabric.api.event.Event

/**
 * Wraps an existing Fabric [Event] into a [Hook], using [transform] to map recode listeners to its
 * specification.
 */
fun <C, R, H> wrapFabricEvent(event: Event<H>, transform: (HookListener<C, R>) -> H): WrappedHook<C, R, H> =
    wrapFabricEventWithPhases<_, _, _, EventPhase>(event, transform)

/**
 * @see wrapFabricEvent
 * @see createHookableWithPhases
 */
fun <C, R, H, P : EventPhase> wrapFabricEventWithPhases(
    event: Event<H>,
    transform: (HookListener<C, R>) -> H
): WrappedPhasedHook<C, R, H, P> {
    return EventWrapper(event, transform)
}

private open class EventWrapper<C, R, H, P : EventPhase>(
    override val fabricEvent: Event<H>,
    private val transform: (HookListener<C, R>) -> H
) : WrappedPhasedHook<C, R, H, P> {
    override val contextFlow: Flow<C> by lazy {
        MutableSharedFlow<C>().also { flow ->
            transformAndRegister { context, result ->
                runBlocking { flow.emit(context) }
                result
            }
        }
    }

    @Deprecated("Use hookFrom")
    override fun register(listener: HookListener<C, R>) = transformAndRegister(listener)

    private fun transformAndRegister(listener: HookListener<C, R>) =
        fabricEvent.register(transform(listener))

    override fun listenFrom(module: HookableModule, listener: HookListener<C, R>) =
        fabricEvent.register(transformFrom(module, listener))

    override fun listenFrom(module: HookableModule, phase: P, listener: HookListener<C, R>) =
        fabricEvent.register(phase.id, transformFrom(module, listener))

    private inline fun transformFrom(module: RModule, crossinline listener: HookListener<C, R>) =
        transform { context, result ->
            if (module.isEnabled) listener(context, result) else result
        }
}

/**
 * Constructs a [CustomPhasedHook].
 */
@Suppress("FunctionName")
fun <C, R : Any, P : EventPhase> CustomPhasedHookable(fabricEvent: Event<HookListener<C, R>>): CustomPhasedHook<C, R, P> =
    object : CustomPhasedHook<C, R, P>, EventWrapper<C, R, HookListener<C, R>, P>(fabricEvent, { it }) {
        override val prevResult get() = _prevResult

        @Suppress("ObjectPropertyName")
        private var _prevResult: R? = null

        override fun run(context: C, initialValue: R) = invoker(context, initialValue)
            .also { _prevResult = it }
    }