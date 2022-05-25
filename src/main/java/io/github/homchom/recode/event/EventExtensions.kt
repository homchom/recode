@file:JvmName("EventExtensions")

package io.github.homchom.recode.event

import io.github.homchom.recode.event.EventResult.*
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

inline fun <reified T> createEvent(noinline factory: (Array<T>) -> T): Event<T> =
    EventFactory.createArrayBacked(T::class.java, factory)

val <T> Event<T>.call: T get() = invoker()

inline fun <T> handleEventWithResult(
    listeners: Array<T>,
    interaction: T.() -> EventResult
): EventResult {
    for (listener in listeners) {
        interaction(listener).also { if (it != PASS) return it }
    }
    return PASS
}

fun <A> handleEventWithResult(listeners: Array<(A) -> EventResult>, a: A) =
    handleEventWithResult<(A) -> EventResult>(listeners) { invoke(a) }