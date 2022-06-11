package io.github.homchom.recode.event

import net.fabricmc.fabric.api.event.Event

/**
 * Creates a Fabric event without a result.
 *
 * @param T the event context (parameters).
 */
fun <T> createHook() = RHook(createFabricEvent<HookListener<T>>(::hookListenerOf))

/**
 * @see createHook
 */
inline fun <T, reified P : Enum<P>> createHookWithPhases() =
    RHook(createFabricEventWithPhases<HookListener<T>, P>(::hookListenerOf))

fun <T> hookListenerOf(listeners: Array<HookListener<T>>) = HookListener<T> { context ->
    for (listener in listeners) listener(context)
}

// TODO: make value class when kotlin migration is complete
class RHook<T>(override val delegate: Event<HookListener<T>>) : FabricEvent<HookListener<T>> {
    operator fun invoke(context: T) = invoker(context)
}

fun interface HookListener<T> {
    operator fun invoke(context: T)
}