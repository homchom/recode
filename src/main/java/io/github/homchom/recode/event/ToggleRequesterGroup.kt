package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.util.coroutines.getAndInvert
import kotlinx.coroutines.selects.select
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration

/**
 * Creates a [ToggleRequesterGroup] with the given [start] and conditional tests for current state.
 *
 * @param enabledPredicate Should return true if the state is enabled.
 * @param enabledTests Tests to run when enabled.
 * @param disabledTests Tests to run when disabled. The success of these tests
 * must be mutually exclusive with that of [enabledTests].
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
    return ShortCircuitToggle(basis, start, timeoutDuration, enabledPredicate,
        enabledTests,
        disabledTests
    )
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
    private val enabledPredicate: () -> Boolean,
    enabledTests: RequesterTrial.Tester<T, B, Unit>,
    disabledTests: RequesterTrial.Tester<T, B, Unit>
) : ToggleRequesterGroup<T> {
    private val futureState = AtomicBoolean(false) // valid iff shouldPredict

    private val predictedState get() = if (shouldPredict) futureState.get() else enabledPredicate()

    private val shouldPredict: Boolean get() =
        toggle.activeRequests > 1 || enable.activeRequests > 1 || disable.activeRequests > 1

    override val toggle = requester(
        trial(basis,
            start = { input: T? ->
                start(input)
                if (shouldPredict) {
                    futureState.getAndInvert()
                } else {
                    futureState.set(!enabledPredicate())
                }
            },
            tests = { input, baseContext, isRequest ->
                val enabledResult = enabledTests.runTestsIn(this, input, baseContext, isRequest)
                val disabledResult = disabledTests.runTestsIn(this, input, baseContext, isRequest)
                suspending {
                    when {
                        enabledResult == null -> disabledResult?.await()
                        disabledResult == null -> enabledResult.await()
                        else -> select {
                            enabledResult.onAwait { it }
                            disabledResult.onAwait { it }
                        }
                    }
                }
            }
        ),
        timeoutDuration = timeoutDuration
    )

    override val enable = requester(
        shortCircuitTrial(basis,
            start = { input: T? ->
                if (predictedState) Unit else {
                    start(input)
                    futureState.set(true)
                    null
                }
            },
            tests = { input, baseContext, isRequest ->
                val result = enabledTests.runTestsIn(this, input, baseContext, isRequest)
                suspending { result?.await() }
            }
        ),
        timeoutDuration = timeoutDuration
    )

    override val disable = requester(
        shortCircuitTrial(basis,
            start = { input: T? ->
                if (predictedState) {
                    start(input)
                    futureState.set(false)
                    null
                } else Unit
            },
            tests = { input, baseContext, isRequest ->
                val result = disabledTests.runTestsIn(this, input, baseContext, isRequest)
                suspending { result?.await() }
            }
        ),
        timeoutDuration = timeoutDuration
    )
}