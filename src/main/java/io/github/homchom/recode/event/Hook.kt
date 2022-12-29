package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.HookableModule
import kotlinx.coroutines.flow.Flow
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

typealias HookListener<T, R> = (T, R) -> R

/**
 * A synchronous, *opaque* [REvent] that can be added to via listeners in modules, which are invoked in order when
 * the event is run and if the module is enabled.
 *
 * @param T The event context type (for parameters).
 * @param R The event result type.
 *
 * @see AsyncEvent
 * @see Detector
 */
interface Hook<T, R> {
    /**
     * A [Flow] that emits the context of each hook invocation. Useful for receiving future invocations
     * as an asynchronous stream.
     */
    val contextFlow: Flow<T>

    @Deprecated("Create and/or hook from a module instead")
    fun register(listener: HookListener<T, R>)

    fun listenFrom(module: HookableModule, listener: HookListener<T, R>)
}

/**
 * A [Hook] with phases of type [P].
 *
 * @see EventFactory.createWithPhases
 */
interface PhasedHook<T, R, P : EventPhase> : Hook<T, R> {
    fun listenFrom(module: HookableModule, phase: P, listener: HookListener<T, R>)
}

/**
 * Listens to this [Hook] without affecting its result.
 */
inline fun <T, R> Hook<T, R>.unitListenFrom(module: HookableModule, crossinline hook: (T) -> Unit) =
    listenFrom(module) { context, result ->
        hook(context)
        result
    }

/**
 * Listens to this [PhasedHook] without affecting its result.
 */
inline fun <T, R, P : EventPhase> PhasedHook<T, R, P>.unitListenFrom(
    module: HookableModule,
    phase: P,
    crossinline hook: (T) -> Unit
) {
    listenFrom(module, phase) { context, result ->
        hook(context)
        result
    }
}

/**
 * A [Hook] wrapper for a Fabric [Event] with an [invoker] for running the event.
 *
 * @param L The raw listener type.
 */
interface WrappedHook<T, R, L> : Hook<T, R> {
    val fabricEvent: Event<L>

    val invoker: L get() = fabricEvent.invoker()
}

/**
 * @see WrappedHook
 * @see PhasedHook
 */
interface WrappedPhasedHook<T, R, H, P : EventPhase> : WrappedHook<T, R, H>, PhasedHook<T, R, P>

/**
 * A wrapper for a Fabric [Event] phase.
 *
 * @see EventFactory.createWithPhases
 */
interface EventPhase {
    val id: ResourceLocation
}