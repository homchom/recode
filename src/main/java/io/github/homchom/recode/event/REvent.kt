package io.github.homchom.recode.event

import io.github.homchom.recode.id
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.resources.ResourceLocation

typealias Listener<C, R> = (C, R) -> R

inline fun <reified C> createFabricEvent(noinline factory: (Array<C>) -> C): Event<C> =
    EventFactory.createArrayBacked(C::class.java, factory)

inline fun <reified C, reified P : Enum<P>> createFabricEventWithPhases(
    noinline factory: (Array<C>) -> C
): Event<C> {
    return EventFactory.createWithPhases(
        C::class.java,
        factory,
        *enumValues<P>().mapTo(mutableListOf()) { id(it.name.lowercase()) }.toTypedArray()
    )
}

/**
 * Wraps an existing event into an [REvent], using [transform] to map recode listeners to its
 * specification.
 */
fun <C, R, L> wrapEvent(event: Event<L>, transform: (Listener<C, R>) -> L) =
    WrappedEvent(event, transform)

/**
 * A wrapper for a Fabric event.
 *
 * @param C The event context type (for parameters).
 * @param R The event result type.
 * @param L The event listener type.
 *
 * @see Event
 */
sealed interface REvent<C, R, L> {
    val delegate: Event<L>

    val invoker: L get() = delegate.invoker()

    /**
     * Adds a listener to this event, which is invoked when the event is run.
     */
    fun listen(listener: Listener<C, R>)

    fun addPhaseOrdering(first: ResourceLocation, second: ResourceLocation) =
        delegate.addPhaseOrdering(first, second)
}

/**
 * @see wrapEvent
 */
@Suppress("MemberVisibilityCanBePrivate")
class WrappedEvent<C, R, L>(
    override val delegate: Event<L>,
    private val transform: (Listener<C, R>) -> L
) : REvent<C, R, L> {
    override fun listen(listener: Listener<C, R>) = delegate.register(transform(listener))
}