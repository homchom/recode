package io.github.homchom.recode.event

import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull

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
    lifecycle: Listenable<*>,
    private val trial: RequesterTrial<ToggleRequesterGroup.Input<T>, Boolean>,
) : ToggleRequesterGroup<T> {
    override val toggle: RequesterModule<T, Unit> =
        requester(lifecycle, naiveTrial(null, ::toggle))

    override val enable: RequesterModule<T, Unit> =
        requester(lifecycle, naiveTrial(true, ::enable))

    override val disable: RequesterModule<T, Unit> =
        requester(lifecycle, naiveTrial(false, ::disable))

    private inline fun naiveTrial(
        desiredState: Boolean?,
        crossinline requester: () -> Requester<T, Unit>
    ): RequesterTrial<T, Unit> {
        return trial(
            trial.basis,
            start = { input: T ->
                val isDesired = trial.start(input.wrap(desiredState)) == desiredState
                isDesired.unitOrNull()
            },
            tests = { input, _, isRequest ->
                val results = trial.supplyResultsFrom(this)
                suspending {
                    val state = results
                        .mapNotNull { it.supplyIn(this, input?.wrap(desiredState), isRequest)?.await() }
                        .first()
                    if (state != desiredState && isRequest) requester().requestFrom(this, input!!)
                }
            }
        )
    }

    fun T.wrap(desiredState: Boolean?) = ToggleRequesterGroup.Input(this, desiredState)
}