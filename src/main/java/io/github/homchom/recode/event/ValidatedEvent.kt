package io.github.homchom.recode.event

/**
 * @see createValidatedEvent
 */
class ValidatedEvent<C>(val event: CustomEvent<C, Boolean>) : CustomEvent<C, Boolean> by event {
    operator fun invoke(context: C) = invoke(context, true)
}

/**
 * Creates an [REvent] with a boolean result; this should be used for events whose listeners "validate"
 * it and determine whether the action that caused it should proceed.
 */
fun <C> createValidatedEvent() = ValidatedEvent(createEvent<C, Boolean>())

/**
 * @see createValidatedEvent
 * @see createEventWithPhases
 */
inline fun <reified C, reified P : Enum<P>> createValidatedEventWithPhases() =
    ValidatedEvent(createEventWithPhases<C, Boolean, P>())