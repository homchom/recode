package io.github.homchom.recode.event.trial

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.RequesterModule
import io.github.homchom.recode.lifecycle.ModuleDetail
import io.github.homchom.recode.lifecycle.ModuleFlavor
import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Creates a [ToggleRequesterGroup] with the given [trial] for toggling.
 *
 * The returned toggle is *naive*, meaning it assumes the request will yield the desired state and, if not,
 * runs the request again. Prefer explicit toggle requests without false positives when possible.
 *
 * If this function is given a trial with a [io.github.homchom.recode.event.Validated] basis, the base context
 * will be hidden when needed to avoid duplicate notifications (see [Trial.Hideable]).
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
    val toggle: RequesterModule<T, Unit>
    val enable: RequesterModule<T, Unit>
    val disable: RequesterModule<T, Unit>
}

private class NaiveToggle<T>(
    name: String,
    lifecycle: Listenable<*>,
    trial: RequesterTrial<T, Boolean>
) : ToggleRequesterGroup<T & Any> {
    override val toggle: RequesterModule<T & Any, Unit>
    override val enable: RequesterModule<T & Any, Unit>
    override val disable: RequesterModule<T & Any, Unit>

    init {
        fun detail(desiredState: Boolean?, requester: () -> Requester<T & Any, Unit>) = ModuleFlavor { module ->
            val delegate = requesterDetail(name, lifecycle, shortCircuitTrial(
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
                            retryModule.launch(Dispatchers.Default) {
                                requester().requestFrom(this@suspending, input!!)
                            }
                        }
                    }
                }
            ))

            delegate.applyTo(module)
        }

        toggle = module(detail(null, ::toggle))
        enable = module(detail(true, ::enable))
        disable = module(detail(false, ::disable))
    }

    private val retryModule = module(ModuleDetail.Exposed) { module ->
        module.extend(toggle, enable, disable)
        module
    }
}