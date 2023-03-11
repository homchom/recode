package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.HookableModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.*
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

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
fun <T, R, L, P : EventPhase> wrapFabricEventWithPhases(
    event: Event<L>,
    transform: (HookListener<T, R>) -> L
): WrappedPhasedHook<T, R, L, P> {
    return EventWrapper(event, transform)
}

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

private open class EventWrapper<T, R, L, P : EventPhase>(
    private val fabricEvent: Event<L>,
    private val transform: (HookListener<T, R>) -> L
) : WrappedPhasedHook<T, R, L, P> {
    private val async = createEvent<T>()

    override val invoker: L get() = fabricEvent.invoker()

    init {
        transformAndRegister { context, result ->
            async.run(context)
            result
        }
    }

    override fun getNotificationsFrom(module: ExposedModule) = async.getNotificationsFrom(module)

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

        private var _prevResult: R? = null

        override fun run(context: T, initialValue: R) = invoker(context, initialValue)
            .also { _prevResult = it }
    }
}

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