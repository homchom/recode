package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.nullable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.produceIn
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

/**
 * Creates a [DetectorModule] that runs via one or more [DetectorTrial] objects.
 *
 * Note that this implementation catches [RequestTimeoutException] with suspending trial results for convenience;
 * such cases are treated as trial failures.
 */
fun <T : Any, R : Any> detector(
    primaryTrial: DetectorTrial<T, R>,
    vararg secondaryTrials: DetectorTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): DetectorModule<T, R> {
    val detail = TrialDetector(listOf(primaryTrial, *secondaryTrials), timeoutDuration)
    return SimpleDetectorModule(detail, module(detail))
}

/**
 * Creates a [RequesterModule] that runs via one or more [RequesterTrial] objects.
 *
 * Note that this implementation catches [RequestTimeoutException] with suspending trial results for convenience;
 * such cases are treated as trial failures.
 */
fun <T : Any, R : Any> requester(
    primaryTrial: RequesterTrial<T, R>,
    vararg secondaryTrials: DetectorTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): RequesterModule<T, R> {
    val detail = TrialRequester(primaryTrial, secondaryTrials, timeoutDuration)
    return SimpleRequesterModule(detail, module(detail))
}

private sealed class DetectorDetail<T : Any, R : Any> : Detector<T, R>, ModuleDetail {
    protected abstract val trials: List<Trial<T, R>>

    private val event = createEvent<R, R> { it }

    private val entries = ConcurrentLinkedQueue<TrialEntry<T, R>>()

    override val prevResult: R? get() = event.prevResult

    override fun getNotificationsFrom(module: RModule) = event.getNotificationsFrom(module)

    override fun ExposedModule.onEnable() = onEnableImpl(this)

    @OptIn(DelicateCoroutinesApi::class)
    protected fun onEnableImpl(module: ExposedModule) {
        for (trial in trials) module.launch {
            trial.supplyResultsFrom(module).collect { supplier ->
                val trialJob = Job(module.coroutineContext.job)
                if (entries.isEmpty()) module.considerEntry(null, supplier, trialJob)

                val iterator = entries.iterator()
                for (entry in iterator) {
                    if (entry.responses.isClosedForSend) {
                        iterator.remove()
                        continue
                    }
                    module.considerEntry(entry, supplier, trialJob)
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun CoroutineModule.considerEntry(
        entry: TrialEntry<T, R>?,
        supplier: Trial.ResultSupplier<T, R>,
        trialJob: Job,
    ) {
        val entryScope = CoroutineScope(coroutineContext + Job(trialJob))
        val result = nullable {
            val trialScope = TrialScope(this@considerEntry, this@nullable, entryScope)
            supplier.supplyIn(trialScope, entry?.input, entry?.isRequest ?: false)
        }
        val entryJob = entryScope.launch(start = CoroutineStart.UNDISPATCHED) {
            if (result == null) {
                entry?.responses?.trySend(null)
                return@launch
            }

            val awaited = try {
                result.await()
            } catch (e: RequestTimeoutException) {
                null
            }
            yield()
            entry?.responses?.trySend(awaited)

            if (awaited != null) {
                event.run(awaited)
                trialJob.cancel("Trial produced non-null response")
            }
        }
        entryJob.invokeOnCompletion { entryScope.cancel("TrialEntry consideration completed") }
    }

    override fun ExposedModule.onLoad() {}
    override fun ExposedModule.onDisable() {}
    override fun children() = emptyModuleList()

    override fun detectFrom(module: RModule, input: T?) = responseFlow(input, false)

    protected fun responseFlow(input: T?, isRequest: Boolean) = flow {
        coroutineScope {
            val responses = Channel<R?>(Channel.UNLIMITED)
            try {
                // add entry after all current detection loops
                launch(RecodeDispatcher()) {
                    yield()
                    entries += TrialEntry(isRequest, input, responses)
                }
                while (isActive) {
                    val response = withTimeoutOrNull(timeoutDuration) { responses.receive() }
                    emit(response)
                }
            } finally {
                responses.close()
            }
        }
    }
}

private data class TrialEntry<T : Any, R : Any>(
    val isRequest: Boolean,
    val input: T?,
    val responses: SendChannel<R?>
)

private class TrialDetector<T : Any, R : Any>(
    override val trials: List<DetectorTrial<T, R>>,
    override val timeoutDuration: Duration
) : DetectorDetail<T, R>()

private class TrialRequester<T : Any, R : Any>(
    primaryTrial: RequesterTrial<T, R>,
    secondaryTrials: Array<out DetectorTrial<T, R>>,
    override val timeoutDuration: Duration
) : DetectorDetail<T, R>(), Requester<T, R> {
    override val trials = listOf(primaryTrial, *secondaryTrials)
    private val start = primaryTrial.start

    override val activeRequests get() = _activeRequests.get()

    private val _activeRequests = AtomicInteger(0)

    override fun ExposedModule.onEnable() {
        onEnableImpl(this)
        _activeRequests.set(0)
    }

    override suspend fun requestFrom(module: RModule, input: T) = withContext(NonCancellable) {
        val detectChannel = responseFlow(input, true)
            .filterNotNull()
            .produceIn(this)

        _activeRequests.incrementAndGet()
        try {
            val response = start(input)
                ?: withTimeoutOrNull(timeoutDuration) { detectChannel.receive() }
                ?: throw RequestTimeoutException(input)
            coroutineContext.cancelChildren()
            response
        } finally {
            _activeRequests.decrementAndGet()
        }
    }
}

private open class SimpleDetectorModule<T : Any, R : Any>(
    private val detail: DetectorDetail<T, R>,
    module: RModule
) : DetectorModule<T, R>, RModule by module {
    override val timeoutDuration get() = detail.timeoutDuration
    override val prevResult get() = detail.prevResult

    override fun getNotificationsFrom(module: RModule): Flow<R> {
        module.depend(this)
        return detail.getNotificationsFrom(module)
    }

    override fun detectFrom(module: RModule, input: T?): Flow<R?> {
        module.depend(this)
        return detail.detectFrom(module, input)
    }
}

private class SimpleRequesterModule<T : Any, R : Any>(
    private val detail: TrialRequester<T, R>,
    module: RModule
) : SimpleDetectorModule<T, R>(detail, module), RequesterModule<T, R> {
    override val activeRequests get() = detail.activeRequests

    override suspend fun requestFrom(module: RModule, input: T): R {
        module.depend(this)
        return detail.requestFrom(module, input)
    }
}