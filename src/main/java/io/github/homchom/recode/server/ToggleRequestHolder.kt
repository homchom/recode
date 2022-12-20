package io.github.homchom.recode.server

import io.github.homchom.recode.event.ValidatedEvent

/**
 * Constructs a [ToggleRequestHolder].
 *
 * @param enabledPredicate Whether the toggle should be considered enabled.
 */
fun <T, I : Any> toggleRequestHolder(
    event: ValidatedEvent<T>,
    executor: suspend (I) -> Unit,
    enabledPredicate: () -> Boolean,
    enabledMatcher: RequestMatcher<T, I, Unit>,
    disabledMatcher: RequestMatcher<T, I, Unit>
): ToggleRequestHolder<T, I> {
    return object : ToggleRequestHolder<T, I> {
        override val toggle by defineRequest(
            event, executor, if (enabledPredicate()) enabledMatcher else disabledMatcher
        )

        override val enable by defineShortCircuitRequest(
            event,
            executor = { input: I ->
                if (enabledPredicate()) Unit else {
                    executor(input)
                    null
                }
            },
            enabledMatcher
        )

        override val disable by defineShortCircuitRequest(
            event,
            executor = { input: I ->
                if (!enabledPredicate()) Unit else {
                    executor(input)
                    null
                }
            },
            disabledMatcher
        )
    }
}

/**
 * An object that holds three response-less [Request] members, representing toggling, enabling, and disabling some
 * state respectively.
 */
interface ToggleRequestHolder<T, I : Any> {
    val toggle: Request<T, I, Unit>
    val enable: Request<T, I, Unit>
    val disable: Request<T, I, Unit>
}