package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import kotlin.time.Duration

/**
 * Creates a [ToggleRequesterGroup] with the given [start] and conditional tests for current state.
 *
 * @param enabledPredicate Should return true if the state is enabled.
 * @param enabledTests Tests to run when enabled.
 * @param disabledTests Tests to run when disabled.
 *
 * @see RequesterTrial
 */
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

/**
 * @see toggleRequesterGroup
 * @see RequesterTrial.NullaryTester
 */
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

/**
 * A group of [Requester] objects for a toggleable state.
 */
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
        shortCircuitTrial(
            basis = basis,
            start = { input ->
                if (enabledPredicate()) Unit else {
                    start(input)
                    null
                }
            },
            tests = enabledTests
        ),
        timeoutDuration = timeoutDuration
    )

    override val disable = requester(
        shortCircuitTrial(
            basis = basis,
            start = { input ->
                if (enabledPredicate()) {
                    start(input)
                    null
                } else Unit
            },
            tests = disabledTests
        ),
        timeoutDuration = timeoutDuration
    )
}