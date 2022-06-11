@file:JvmName("EventValidation")

package io.github.homchom.recode.event

typealias RValidatedEvent<T> = REvent<T, EventValidator>

/**
 * Creates an event with a boolean result; this should be used for events whose listeners "validate"
 * it and determine whether the action that caused it should proceed.
 *
 * @see createEvent
 */
fun <T> createValidatedEvent() = createEvent<T, EventValidator>()

/**
 * Invokes this event and returns its result.
 */
fun <T> RValidatedEvent<T>.validate(context: T) = invoke(context, EventValidator()).isValid

class EventValidator @JvmOverloads constructor(var isValid: Boolean = true)