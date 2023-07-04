package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.ModuleDetail
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
 * This function should usually be given a trial with a [Validated] basis that can invalidate when the state
 * is undesired (e.g. to avoid duplicate chat messages).
 *
 * @param trial Should yield a true result if the state is enabled, and false if it is disabled.
 *
 * @see requester
 */
fun <T : Any> toggleRequesterGroup(
    lifecycle: Listenable<*>,
    trial: RequesterTrial<ToggleRequesterGroup.Input<T>, Boolean>
): ToggleRequesterGroup<T> {
    return NaiveToggle(lifecycle, trial)
}

/**
 * A specialized group of [Requester] objects for a toggleable state.
 */
sealed interface ToggleRequesterGroup<T : Any> {
    val toggle: RequesterModule<T, Unit>
    val enable: RequesterModule<T, Unit>
    val disable: RequesterModule<T, Unit>

    data class Input<T : Any>(val value: T, val shouldBeEnabled: Boolean?)
}

private class NaiveToggle<T : Any>(
    private val lifecycle: Listenable<*>,
    private val trial: RequesterTrial<ToggleRequesterGroup.Input<T>, Boolean>,
) : ToggleRequesterGroup<T> {
    override val toggle: RequesterModule<T, Unit> =
        module(naiveRequesterDetail(null, ::toggle))

    override val enable: RequesterModule<T, Unit> =
        module(naiveRequesterDetail(true, ::enable))

    override val disable: RequesterModule<T, Unit> =
        module(naiveRequesterDetail(false, ::disable))

    private val retryModule = module(ModuleDetail.Exposed) {
        extend(toggle, enable, disable)
    }

    private fun naiveRequesterDetail(desiredState: Boolean?, requester: () -> Requester<T, Unit>) =
        ModuleDetail<ExposedModule, RequesterModule<T, Unit>> { module ->
            val delegate = requesterDetail(lifecycle, trial(
                trial.supplyResultsFrom(module).asListenable(),
                start = { input: T ->
                    val isDesired = trial.start(input.wrap(desiredState)) == desiredState
                    isDesired.unitOrNull()
                },
                tests = { input, supplier, isRequest ->
                    suspending {
                        val state = supplier
                            .supplyIn(this, input?.wrap(desiredState), isRequest)
                            ?.await()
                        if (state != desiredState && isRequest) {
                            retryModule.launch(Dispatchers.Default) {
                                requester().requestFrom(this@suspending, input!!)
                            }
                        }
                    }
                }
            ))

            delegate.applyTo(module)
        }

    fun T.wrap(desiredState: Boolean?) = ToggleRequesterGroup.Input(this, desiredState)
}