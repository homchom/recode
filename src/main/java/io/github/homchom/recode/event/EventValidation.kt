@file:JvmName("EventValidation")

package io.github.homchom.recode.event

typealias RValidatedEvent<T> = REvent<T, EventValidator>

fun <T> createValidatedEvent() = createEvent<T, EventValidator>()

fun <T> RValidatedEvent<T>.validate(context: T) = invoke(context, EventValidator()).isValid

class EventValidator @JvmOverloads constructor(var isValid: Boolean = true)