@file:JvmName("Hook")

package io.github.homchom.recode.event

/**
 * Creates a custom [REvent] without a result.
 *
 */
fun <C> createHook() = createEvent<C, Unit>()

/**
 * Runs this event.
 */
operator fun <C> CustomEvent<C, Unit>.invoke(context: C) = invoke(context, Unit)