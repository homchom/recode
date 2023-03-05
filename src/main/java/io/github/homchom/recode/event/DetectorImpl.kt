package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.attempt
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration

fun <T : Any, R : Any> detector(
    vararg trials: DetectorTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): DetectorModule<T, R> {
    val detail = TrialDetector(trials.toList(), timeoutDuration)
    return SimpleDetectorModule(detail, module(detail))
}

fun <T : Any, R : Any> requester(
    vararg trials: RequesterTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): RequesterModule<T, R> {
    val detail = TrialRequester(trials.toList(), timeoutDuration)
    return SimpleRequesterModule(detail, module(detail))
}

private sealed class DetectorDetail<T : Any, R : Any, S> : Detector<T, R>, ModuleDetail {
    protected abstract val trials: List<Trial<S>>
    abstract val timeoutDuration: Duration

    private val event = createEvent<R>()

    private val queue = ConcurrentLinkedQueue<TrialEntry<T, R>>()

    override fun getNotificationsFrom(module: ExposedModule) = event.getNotificationsFrom(module)

    override fun ExposedModule.onEnable() {
        for (trial in trials) trial.supplyResultsFrom(this).listenEach { supplier ->
            suspend fun getResponse(entry: TrialEntry<T, *>?) =
                trialScope { runTests(supplier, entry) ?: fail() }

            if (queue.isEmpty()) {
                val response = getResponse(null)
                if (response != null) event.run(response)
            } else while (queue.isNotEmpty()) {
                if (queue.peek()!!.basis != trial.basis) continue
                val entry: TrialEntry<T, R> = queue.poll()
                val response = getResponse(entry)
                if (response != null || queue.isEmpty()) {
                    entry.response.complete(response)
                    if (response != null) event.run(response)
                    break
                }
            }
        }
    }

    override fun ExposedModule.onLoad() {}
    override fun ExposedModule.onDisable() {}
    override fun children() = emptyModuleList()

    override suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>?) =
        addDetectAndAwait(input, basis) { block ->
            attempt(timeoutDuration, block)
        }

    override suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>?, attempts: UInt) =
        addDetectAndAwait(input, basis) { block ->
            attempt(attempts) { block() }
        }

    private suspend inline fun addDetectAndAwait(
        input: T?,
        basis: Listenable<*>?,
        attemptFunc: (block: suspend () -> R?) -> R?
    ): R? {
        return addAndAwait(attemptFunc) { DetectEntry(input, basis ?: trials[0].basis, it) }
    }

    protected suspend inline fun addAndAwait(
        attemptFunc: (suspend () -> R?) -> R?,
        crossinline entry: (CompletableDeferred<R?>) -> TrialEntry<T, R>
    ): R? {
        return attemptFunc {
            val deferredResponse = CompletableDeferred<R?>()

            // TODO: better way to do this with referential equality?
            withContext(Dispatchers.IO) { yield() }

            queue += entry(deferredResponse)
            withTimeoutOrNull(timeoutDuration) { deferredResponse.await() }
        }
    }

    protected abstract suspend fun TrialScope.runTests(supplier: S, entry: TrialEntry<T, *>?): R?

    private data class DetectEntry<T : Any, R : Any>(
        override val input: T?,
        override val basis: Listenable<*>,
        override val response: CompletableDeferred<R?>
    ) : TrialEntry<T, R>
}

private sealed interface TrialEntry<T : Any, R : Any> {
    val input: T?
    val basis: Listenable<*>
    val response: CompletableDeferred<R?>
}

private class TrialDetector<T : Any, R : Any>(
    override val trials: List<DetectorTrial<T, R>>,
    override val timeoutDuration: Duration
) : DetectorDetail<T, R, DetectorTrial.ResultSupplier<T, R>>() {
    override suspend fun TrialScope.runTests(
        supplier: DetectorTrial.ResultSupplier<T, R>,
        entry: TrialEntry<T, *>?
    ): R? {
        return supplier.supplyIn(this, entry?.input)
    }
}

private class TrialRequester<T : Any, R : Any>(
    override val trials: List<RequesterTrial<T, R>>,
    override val timeoutDuration: Duration
) : DetectorDetail<T, R, RequesterTrial.ResultSupplier<T, R>>(), Requester<T, R> {
    init {
        require(trials.isNotEmpty())
    }

    override suspend fun TrialScope.runTests(
        supplier: RequesterTrial.ResultSupplier<T, R>,
        entry: TrialEntry<T, *>?
    ): R? {
        return supplier.supplyIn(this, entry?.input, entry is RequestEntry)
    }

    override suspend fun requestFrom(module: RModule, input: T) =
        addRequestAndAwait(input) { block ->
            attempt(timeoutDuration, block)
        }

    override suspend fun requestNextFrom(module: RModule, input: T, attempts: UInt) =
        addRequestAndAwait(input) { block ->
            attempt(attempts) { block() }
        }

    private suspend inline fun addRequestAndAwait(
        input: T,
        attemptFunc: (block: suspend () -> R?) -> R?
    ): R {
        trials[0].start(input)?.let { return it }
        return addAndAwait(attemptFunc) { RequestEntry(input, trials[0].basis, it) }
            ?: error("Request trial failed")
    }

    private data class RequestEntry<T : Any, R : Any>(
        override val input: T?,
        override val basis: Listenable<*>,
        override val response: CompletableDeferred<R?>
    ) : TrialEntry<T, R>
}

private open class SimpleDetectorModule<T : Any, R : Any>(
    private val detail: DetectorDetail<T, R, *>,
    module: RModule
) : DetectorModule<T, R>, RModule by module {
    override fun getNotificationsFrom(module: ExposedModule): Flow<R> {
        module.depend(this)
        return detail.getNotificationsFrom(module)
    }

    override suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>?, attempts: UInt): R? {
        module.depend(this)
        return detail.checkNextFrom(module, input, basis, attempts)
    }

    override suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>?): R? {
        module.depend(this)
        return detail.detectFrom(module, input, basis)
    }
}

private class SimpleRequesterModule<T : Any, R : Any>(
    private val detail: TrialRequester<T, R>,
    module: RModule
) : SimpleDetectorModule<T, R>(detail, module), RequesterModule<T, R> {
    override suspend fun requestNextFrom(module: RModule, input: T, attempts: UInt): R {
        module.depend(this)
        return detail.requestNextFrom(module, input, attempts)
    }

    override suspend fun requestFrom(module: RModule, input: T): R {
        module.depend(this)
        return detail.requestFrom(module, input)
    }
}