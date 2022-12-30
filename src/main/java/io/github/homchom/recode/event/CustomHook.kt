package io.github.homchom.recode.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

/**
 * Creates a custom [Hook].
 *
 * @see CustomHook
 * @see EventFactory.createArrayBacked
 */
fun <T, R : Any> createHook(): CustomHook<T, R> =
    CustomPhasedHookable<_, _, EventPhase>(createFabricEvent(::customHookOf))

/**
 * Creates a custom [Hook] with [phases].
 *
 * @see CustomPhasedHook
 * @see EventFactory.createWithPhases
 */
fun <T, R : Any, P : EventPhase> createHookWithPhases(vararg phases: P) =
    CustomPhasedHookable<T, R, P>(createFabricEventWithPhases(phases, ::customHookOf))

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

    fun test() {
        if (this is DependentHook) println(abc)
    }
}

/**
 * @see CustomHook
 * @see PhasedHook
 */
interface CustomPhasedHook<T, R : Any, P : EventPhase> : CustomHook<T, R>, PhasedHook<T, R, P>

private inline fun <reified L> createFabricEvent(noinline factory: (Array<L>) -> L): Event<L> =
    EventFactory.createArrayBacked(L::class.java, factory)

private inline fun <reified L> createFabricEventWithPhases(
    phases: Array<out EventPhase>,
    noinline factory: (Array<L>) -> L
): Event<L> {
    val phaseIDs = phases
        .mapTo(mutableListOf()) { it.id }
        .apply { add(Event.DEFAULT_PHASE) }
        .toTypedArray()
    return EventFactory.createWithPhases(L::class.java, factory, *phaseIDs)
}

private fun <T, R> customHookOf(hooks: Array<HookListener<T, R>>): HookListener<T, R> =
    { context, initialValue ->
        hooks.fold(initialValue) { result, hook -> hook(context, result) }
    }