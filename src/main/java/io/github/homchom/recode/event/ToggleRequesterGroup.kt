package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import kotlin.time.Duration

fun <T : Any, B> toggleRequesterGroup(
    basis: Listenable<B>,
    start: TrialStart<T>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    enabledPredicate: () -> Boolean,
    enabledTrial: RequesterTrial<T, B, Unit>,
    disabledTrial: RequesterTrial<T, B, Unit>
) : ToggleRequesterGroup<T> {
    return ShortCircuitToggle(basis, start, timeoutDuration, enabledPredicate, enabledTrial, disabledTrial)
}

inline fun <B> nullaryToggleRequesterGroup(
    basis: Listenable<B>,
    crossinline start: suspend () -> Unit,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    noinline enabledPredicate: () -> Boolean,
    crossinline enabledTrial: NullaryRequesterTrial<B, Unit>,
    crossinline disabledTrial: NullaryRequesterTrial<B, Unit>
) : ToggleRequesterGroup<Unit> {
    return toggleRequesterGroup(basis, { start() }, timeoutDuration, enabledPredicate,
        enabledTrial = { _, baseContext, isRequest -> enabledTrial(baseContext, isRequest) },
        disabledTrial = { _, baseContext, isRequest -> disabledTrial(baseContext, isRequest) }
    )
}

sealed interface ToggleRequesterGroup<T : Any> {
    val toggle: Requester<T, Unit>
    val enable: Requester<T, Unit>
    val disable: Requester<T, Unit>
}

private class ShortCircuitToggle<T : Any, B>(
    basis: Listenable<B>,
    start: TrialStart<T>,
    timeoutDuration: Duration,
    enabledPredicate: () -> Boolean,
    enabledTrial: RequesterTrial<T, B, Unit>,
    disabledTrial: RequesterTrial<T, B, Unit>
) : ToggleRequesterGroup<T> {
    override val toggle = requester(basis, start, timeoutDuration) { input, baseContext, isRequest ->
        if (enabledPredicate()) {
            enabledTrial(input, baseContext, isRequest)
        } else {
            disabledTrial(input, baseContext, isRequest)
        }
    }

    override val enable = shortCircuitRequester(
        basis, startIf(enabledPredicate, start), timeoutDuration, enabledTrial
    )

    override val disable = shortCircuitRequester(
        basis, startIf({ !enabledPredicate() }, start), timeoutDuration, enabledTrial
    )

    private inline fun startIf(
        crossinline predicate: () -> Boolean,
        crossinline start: TrialStart<T>
    ): ShortCircuitTrialStart<T, Unit?> {
        return { input: T ->
            if (predicate()) Unit else {
                start(input)
                null
            }
        }
    }
}