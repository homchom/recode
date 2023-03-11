package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.HookableModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation
import kotlin.time.Duration

typealias HookListener<T, R> = (T, R) -> R

/**
 * Creates a [SharedEvent].
 */
fun <T> createEvent(): SharedEvent<T> = SharedFlowEvent()

/**
 * Creates a [StateEvent].
 */
fun <T> createStateEvent(initialValue: T): StateEvent<T> = StateFlowEvent(initialValue)

/**
 * A shared [Listenable] event that can be [run].
 *
 * @see MutableSharedFlow
 */
interface SharedEvent<T> : Listenable<T> {
    fun run(context: T)
}

/**
 * A shared [StateListenable] that can be [run].
 *
 * @see MutableStateFlow
 */
interface StateEvent<T> : SharedEvent<T>, StateListenable<T>

private class SharedFlowEvent<T> : SharedEvent<T>, Listenable<T> {
    private val flow = MutableSharedFlow<T>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getNotificationsFrom(module: ExposedModule) = flow

    // TODO: remove check()?
    override fun run(context: T) = check(flow.tryEmit(context))
}

private class StateFlowEvent<T>(initialValue: T) : StateEvent<T>, StateListenable<T> {
    private val flow = MutableStateFlow(initialValue)

    override val currentState get() = flow.value

    override fun getNotificationsFrom(module: ExposedModule) = flow

    override fun run(context: T) = flow.update { context }
}

/**
 * A [Listenable] event that can also be "hooked onto" synchronously via listeners in modules, which are
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
 * A custom [WrappedHook] that stores its previous result.
 */
interface CustomHook<T, R : Any> : WrappedHook<T, R, HookListener<T, R>> {
    /**
     * The result of the previous event invocation, or null if the event has not been run.
     */
    val prevResult: R?

    /**
     * Runs this event, transforming [initialValue] into the event result. [initialValue] may
     * be mutated.
     */
    fun run(context: T, initialValue: R): R
}

/**
 * @see CustomHook
 * @see PhasedHook
 */
interface CustomPhasedHook<T, R : Any, P : EventPhase> : CustomHook<T, R>, PhasedHook<T, R, P>

/**
 * A wrapper for a Fabric [Event] phase.
 *
 * @see EventFactory.createWithPhases
 */
interface EventPhase {
    val id: ResourceLocation
}

// TODO: revisit Detector and Requester interfaces (should more be exposed/documented? less?)

/**
 * A [Listenable] that is run algorithmically, based on another Listenable.
 *
 * @param T The type passed as input to the detector.
 * @param R The detector's event context type for detected results.
 *
 * @property timeoutDuration The maximum duration used in detection functions.
 */
interface Detector<T : Any, R : Any> : Listenable<R> {
    val timeoutDuration: Duration

    /**
     * Listens for [basis] invocations from [module] until a match is found.
     *
     * @returns The event result, or null if one could not be found in time.
     */
    suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>? = null): R?

    /**
     * Listens to the next [basis] invocation from [module] and returns a potential match.
     *
     * @returns The event result, or null if there was not a match.
     */
    suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>? = null, attempts: UInt = 1u): R?
}

/**
 * A [Detector] that can execute code to request a result before detecting it.
 */
interface Requester<T : Any, R : Any> : Detector<T, R> {
    /**
     * Makes a request and detects the result.
     *
     * @see detectFrom
     */
    suspend fun requestFrom(module: RModule, input: T): R

    /**
     * Makes a request and detects the result only from the next invocation.
     *
     * @see checkNextFrom
     */
    suspend fun requestNextFrom(module: RModule, input: T, attempts: UInt = 1u): R
}

/**
 * @see Detector
 * @see RModule
 */
interface DetectorModule<T : Any, R : Any> : Detector<T, R>, RModule

/**
 * @see Requester
 * @see RModule
 */
interface RequesterModule<T : Any, R : Any> : Requester<T, R>, RModule