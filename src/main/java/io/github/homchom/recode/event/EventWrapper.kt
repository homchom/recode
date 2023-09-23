package io.github.homchom.recode.event

import io.github.homchom.recode.PowerSink
import net.fabricmc.fabric.api.event.Event

/**
 * Wraps an existing Fabric [Event] into a [Listenable], using [transform] to map recode listeners to its
 * specification.
 */
fun <T, L> wrapFabricEvent(
    event: Event<L>,
    transform: (EventInvoker<T>) -> L
): WrappedEvent<T, L> {
    return EventWrapper(event, transform, createEvent())
}

private class EventWrapper<T, L>(
    private val fabricEvent: Event<L>,
    transform: ((T) -> Unit) -> L,
    private val async: CustomEvent<T, Unit>
) : WrappedEvent<T, L>, PowerSink by async {
    override val notifications by async::notifications

    override val invoker: L get() = fabricEvent.invoker()

    init {
        fabricEvent.register(transform(async::run))
    }
}