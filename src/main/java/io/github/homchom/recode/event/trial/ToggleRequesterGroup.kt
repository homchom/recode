package io.github.homchom.recode.event.trial

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.util.std.unitOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Creates a [ToggleRequesterGroup] with the given [trial] for toggling.
 *
 * The returned toggle is *naive*, meaning it assumes the request will yield the desired state and, if not,
 * runs the request again. Prefer explicit toggle requests without false positives when possible.
 *
 * If this function is given a trial with a [io.github.homchom.recode.event.Validated] basis, the base context
 * should be hidden when needed to avoid duplicate notifications (see [TrialScope.hidden]).
 *
 * @param trial Should yield a true result if the state is enabled, and false if it is disabled.
 *
 * @see requester
 */
fun <T> toggleRequesterGroup(
    name: String,
    lifecycle: Listenable<*>,
    trial: RequesterTrial<T, Boolean>
): ToggleRequesterGroup<T & Any> {
    return NaiveToggle(name, lifecycle, trial)
}

/**
 * A specialized group of [Requester] objects for a toggleable state.
 *
 * Each requester returns a [Boolean], representing the actual detected state (as opposed to the expected state).
 */
sealed interface ToggleRequesterGroup<T : Any> {
    val toggle: Requester<T, Unit>
    val enable: Requester<T, Unit>
    val disable: Requester<T, Unit>
}

private class NaiveToggle<T>(
    name: String,
    lifecycle: Listenable<*>,
    trial: RequesterTrial<T, Boolean>
) : ToggleRequesterGroup<T & Any> {
    override val toggle: Requester<T & Any, Unit>
    override val enable: Requester<T & Any, Unit>
    override val disable: Requester<T & Any, Unit>

    private val power = Power()

    init {
        fun impl(desiredState: Boolean?, reference: () -> Requester<T & Any, Unit>) =
            requester(name, lifecycle, shortCircuitTrial(
                trial.results,
                trial.defaultInput,
                start = { input ->
                    val isDesired = trial.start(input) == desiredState
                    isDesired.unitOrNull()
                },
                tests = { supplier, input, isRequest ->
                    suspending {
                        fun retry(result: Boolean, isRequest: Boolean) =
                            isRequest && (desiredState == true && !result || desiredState == false && result)

                        val state = supplier.supplyIn(this, input, isRequest, ::retry)?.await()
                        if (state != null && retry(state, isRequest)) {
                            power.launch(Dispatchers.Default) {
                                reference().request(input!!, isRequest)
                            }
                        }
                    }
                }
            ))

        toggle = impl(null, ::toggle)
        enable = impl(true, ::enable)
        disable = impl(false, ::disable)

        power.extend(toggle)
        power.extend(enable)
        power.extend(disable)
    }
}