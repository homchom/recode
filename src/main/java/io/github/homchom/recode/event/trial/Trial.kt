package io.github.homchom.recode.event.trial

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Validated
import io.github.homchom.recode.event.map
import kotlinx.coroutines.*

/**
 * Creates a [DetectorTrial] with the given [basis] and [tests].
 *
 * @param defaultInput The input to be substituted if a `null` input is provided.
 */
fun <T, B, R : Any> trial(
    basis: Listenable<B>,
    defaultInput: T,
    tests: DetectorTrial.Tester<T, B, R>
): DetectorTrial<T, R> {
    return BasedTrial(basis, defaultInput, tests)
}

/**
 * Creates a [RequesterTrial] with the given [basis], [start], and [tests].
 */
fun <T, B, R : Any> trial(
    basis: Listenable<B>,
    defaultInput: T,
    start: suspend (input: T & Any) -> Unit,
    tests: Trial.Tester<T, B, R>,
): RequesterTrial<T, R> {
    return shortCircuitTrial(basis, defaultInput, { start(it); null }, tests)
}

/**
 * Creates a short-circuit [RequesterTrial] that can optionally return without requesting when [start] returns a
 * non-null result.
 *
 * @see trial
 */
fun <T, B, R : Any> shortCircuitTrial(
    basis: Listenable<B>,
    defaultInput: T,
    start: suspend (input: T & Any) -> R?,
    tests: Trial.Tester<T, B, R>,
): RequesterTrial<T, R> {
    return BasedRequesterTrial(basis, defaultInput, start, tests)
}

/**
 * An object that can run tests and supply its results. For an explanation of the Trial DSL,
 * see [TrialScope].
 *
 * @property basis The event that the trial is based on, and the starting point of each trial run.
 *
 * @see trial
 */
sealed interface Trial<T, R : Any> {
    val basis: Listenable<*>
    val results: Listenable<ResultSupplier<T & Any, R>>

    val defaultInput: T

    /**
     * A function object that runs [Trial] tests to yield a [TrialResult].
     *
     * @see runTests
     */
    fun interface Tester<T, B, R : Any> {
        /**
         * @see io.github.homchom.recode.event.Detector.detect
         */
        fun TrialScope.runTests(baseContext: B, input: T, isRequest: Boolean): TrialResult<R>?

        fun runTestsIn(scope: TrialScope, input: T, baseContext: B, isRequest: Boolean) =
            scope.runTests(baseContext, input, isRequest)
    }

    /**
     * A function object that supplies [TrialResult] objects. This is used to hide the type information of basis
     * events and contexts; a ResultSupplier is only concerned with the input type [T] and the result type [R].
     *
     * @see supply
     */
    fun interface ResultSupplier<T : Any, R : Any> {
        /**
         * @param isRequest Whether the result is being supplied to a consumer expecting a request. If
         * the trial is a [DetectorTrial], this parameter has no effect.
         * @param hidden An optional [HideCallback].
         */
        fun TrialScope.supply(input: T?, isRequest: Boolean, hidden: HideCallback<R>?): TrialResult<R>?

        fun supplyIn(scope: TrialScope, input: T?, isRequest: Boolean, hidden: HideCallback<R>? = null) =
            scope.supply(input, isRequest, hidden)

        /**
         * A boolean-returning callback to determine whether the supplied result should be [TrialScope.hidden].
         */
        fun interface HideCallback<R : Any> {
            operator fun invoke(result: R, isRequest: Boolean): Boolean
        }
    }
}

/**
 * A [Trial] that supplies to [io.github.homchom.recode.event.Detector] events.
 *
 * **Detector trials should not have visible side effects.** When a detector has multiple entries that
 * would pass a trial, the first entry to complete is sent the result; other trials may continue to run
 * but **are not guaranteed to**.
 */
sealed interface DetectorTrial<T, R : Any> : Trial<T, R> {
    /**
     * A specialized [Trial.Tester] for [DetectorTrial] objects.
     *
     * @see runTests
     */
    fun interface Tester<T, B, R : Any> : Trial.Tester<T, B, R> {
        fun TrialScope.runTests(baseContext: B, input: T): TrialResult<R>?

        override fun TrialScope.runTests(baseContext: B, input: T, isRequest: Boolean) =
            runTests(baseContext, input)
    }
}

/**
 * A [Trial] that supplies to [io.github.homchom.recode.event.Requester] events.
 *
 * @property start The executor function that starts the request.
 */
sealed interface RequesterTrial<T, R : Any> : Trial<T, R> {
    val start: suspend (input: T & Any) -> R?
}

/**
 * A wrapper for a [Deferred] [Trial] result.
 */
class TrialResult<T : Any> private constructor(private val deferred: Deferred<T?>) : Deferred<T?> by deferred {
    constructor(instantValue: T?) : this(CompletableDeferred(instantValue))

    @OptIn(DelicateCoroutinesApi::class)
    constructor(
        asyncBlock: suspend TrialScope.() -> T?,
        scope: CoroutineScope,
        hidden: Boolean = false
    ) : this(
        scope.async {
            try {
                coroutineScope {
                    val trialScope = TrialScope(this, hidden)
                    yield()
                    trialScope.asyncBlock().also { coroutineContext.cancelChildren() }
                }
            } catch (e: TrialScopeException) {
                null
            }
        }
    )
}

private open class BasedTrial<T, B, R : Any>(
    final override val basis: Listenable<B>,
    override val defaultInput: T,
    private val tests: Trial.Tester<T, B, R>
) : DetectorTrial<T, R> {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val results = basis.map { baseContext ->
        Trial.ResultSupplier<T & Any, R> { input, isRequest, hidden ->
            val result = tests.runTestsIn(this, input ?: defaultInput, baseContext, isRequest)

            // handle HideCallbacks
            if (baseContext is Validated && hidden != null) result?.invokeOnCompletion { exception ->
                if (exception != null) return@invokeOnCompletion
                val completed = result.getCompleted()
                if (completed != null && hidden.invoke(completed, isRequest)) {
                    baseContext.invalidate()
                }
            }

            result
        }
    }
}

private class BasedRequesterTrial<T, B, R : Any>(
    basis: Listenable<B>,
    defaultInput: T,
    override val start: suspend (input: T & Any) -> R?,
    tests: Trial.Tester<T, B, R>
) : BasedTrial<T, B, R>(basis, defaultInput, tests), RequesterTrial<T, R>