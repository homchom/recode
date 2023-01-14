package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.HookableModule
import io.github.homchom.recode.lifecycle.RModule
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

typealias HookListener<T, R> = (T, R) -> R

/**
 * A *transparent* [Listenable] that can also be "hooked onto" synchronously via listeners in modules, which are
 * invoked in order when the event is run and if the module is enabled.
 *
 * Hook invocations have an initial value, which can be transformed by all hooked listeners before being
 * returned as the invocation's result. If you do not need a return type, use [SharedEvent] instead.
 *
 * @param T The event context type (for parameters).
 * @param R The event result type.
 */
interface Hook<T, R> : Listenable<T> {
    @Deprecated("Create and/or hook from a module instead")
    fun register(listener: HookListener<T, R>)

    fun hookFrom(module: HookableModule, listener: HookListener<T, R>)
}

/**
 * A [Hook] with phases of type [P].
 *
 * @see EventFactory.createWithPhases
 */
interface PhasedHook<T, R, P : EventPhase> : Hook<T, R> {
    fun hookFrom(module: HookableModule, phase: P, listener: HookListener<T, R>)
}

/**
 * A [Hook] wrapper for a Fabric [Event] with an [invoker] for running the event.
 *
 * @param L The raw listener type.
 */
interface WrappedHook<T, R, L> : Hook<T, R> {
    val invoker: L
}

/**
 * @see WrappedHook
 * @see PhasedHook
 */
interface WrappedPhasedHook<T, R, L, P : EventPhase> : WrappedHook<T, R, L>, PhasedHook<T, R, P>

/**
 * A [Hook] with a boolean result; this should be used for events whose listeners "validate" it and
 * determine whether the action that caused it should proceed.
 */
interface ValidatedHook<T> : Hook<T, Boolean>

/**
 * A [CustomHook] with children. When listened to by a [HookableModule], the children will be implicitly added.
 */
class DependentHook<T, R : Any>(
    private val delegate: CustomHook<T, R>,
    vararg children: RModule
) : CustomHook<T, R> by delegate {
    private val children = children.clone()

    override fun hookFrom(module: HookableModule, listener: HookListener<T, R>) {
        for (child in children) child.addParent(module)
        delegate.hookFrom(module, listener)
    }
}

/**
 * A wrapper for a Fabric [Event] phase.
 *
 * @see EventFactory.createWithPhases
 */
interface EventPhase {
    val id: ResourceLocation
}