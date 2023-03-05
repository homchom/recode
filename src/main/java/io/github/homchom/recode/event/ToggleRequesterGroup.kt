package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import kotlin.time.Duration

fun <T : Any, B> toggleRequesterGroup(
    basis: Listenable<B>,
    start: suspend (input: T?) -> Unit,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    enabledPredicate: () -> Boolean,
    enabledTests: RequesterTrial.Tester<T, B, Unit>,
    disabledTests: RequesterTrial.Tester<T, B, Unit>
) : ToggleRequesterGroup<T> {
    return ShortCircuitToggle(basis, start, timeoutDuration, enabledPredicate, enabledTests, disabledTests)
}

inline fun <B> nullaryToggleRequesterGroup(
    basis: Listenable<B>,
    crossinline start: suspend () -> Unit,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    noinline enabledPredicate: () -> Boolean,
    enabledTests: RequesterTrial.NullaryTester<B, Unit>,
    disabledTests: RequesterTrial.NullaryTester<B, Unit>
) : ToggleRequesterGroup<Unit> {
    return toggleRequesterGroup(basis, { start() }, timeoutDuration, enabledPredicate,
        enabledTests = enabledTests.toUnary(),
        disabledTests = disabledTests.toUnary()
    )
}

sealed interface ToggleRequesterGroup<T : Any> {
    val toggle: RequesterModule<T, Unit>
    val enable: RequesterModule<T, Unit>
    val disable: RequesterModule<T, Unit>
}

private class ShortCircuitToggle<T : Any, B>(
    basis: Listenable<B>,
    start: suspend (input: T?) -> Unit,
    timeoutDuration: Duration,
    enabledPredicate: () -> Boolean,
    enabledTests: RequesterTrial.Tester<T, B, Unit>,
    disabledTests: RequesterTrial.Tester<T, B, Unit>
) : ToggleRequesterGroup<T> {
    override val toggle = requester(
        trial(basis, start) { input, baseContext, isRequest ->
            if (enabledPredicate()) {
                enabledTests.runTestsIn(this, input, baseContext, isRequest)
            } else {
                disabledTests.runTestsIn(this, input, baseContext, isRequest)
            }
        },
        timeoutDuration = timeoutDuration
    )

    override val enable = requester(
        shortCircuitTrial(basis, { if (!enabledPredicate()) start(it) else null }, enabledTests),
        timeoutDuration = timeoutDuration
    )

    override val disable = requester(
        shortCircuitTrial(basis, { if (enabledPredicate()) start(it) else null }, enabledTests),
        timeoutDuration = timeoutDuration
    )
}