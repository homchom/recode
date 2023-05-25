package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.attempt
import io.github.homchom.recode.util.nullable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration

/**
 * Creates a [DetectorModule] that runs via one or more [trials].
 *
 * @see DetectorTrial
 */
fun <T : Any, R : Any> detector(
    vararg trials: DetectorTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): DetectorModule<T, R> {
    val detail = TrialDetector(trials.toList(), timeoutDuration)
    return SimpleDetectorModule(detail, module(detail))
}

/**
 * Creates a [RequesterModule] that runs via one or more [trials].
 *
 * @param debugName A short description of what the requester services. For example,
 * [io.github.homchom.recode.server.ChatLocalRequester] has the debug name "/chat local".
 *
 * @see RequesterTrial
 */
fun <T : Any, R : Any> requester(
    debugName: String,
    vararg trials: RequesterTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): RequesterModule<T, R> {
    val detail = TrialRequester(debugName, trials.toList(), timeoutDuration)
    return SimpleRequesterModule(detail, module(detail))
}

@OptIn(DelicateCoroutinesApi::class)
private val detectorThreadContext = newSingleThreadContext("recode detector thread")

private sealed class DetectorDetail<T : Any, R : Any, S> : Detector<T, R>, ModuleDetail {
    protected abstract val trials: List<Trial<S>>

    private val event = createEvent<R, R> { it }

    private val entries = ConcurrentLinkedQueue<TrialEntry<T, R>>()
    private val responseMutex = Mutex()

    override val prevResult: R? get() = event.prevResult

    override fun getNotificationsFrom(module: RModule) = event.getNotificationsFrom(module)

    @OptIn(DelicateCoroutinesApi::class)
    override fun ExposedModule.onEnable() {
        for (trial in trials) trial.supplyResultsFrom(this).listenEach { supplier ->
            fun getResponse(entry: TrialEntry<T, *>?) = trialScope { runTests(supplier, entry) }
            suspend fun awaitResponse(response: Deferred<R?>) = nullable { response.await() }

            if (entries.isEmpty()) {
                val response = getResponse(null)
                if (response != null) launch {
                    awaitResponse(response)?.let { event.run(it) }
                }
                return@listenEach
            }

            val iterator = entries.iterator()
            var successful = false

            for (entry in iterator) {
                if (entry.responses.isClosedForSend) {
                    iterator.remove()
                    continue
                }
                if (entry.basis != trial.basis) continue

                val response = getResponse(entry)
                if (response == null) {
                    entry.responses.trySend(null)
                } else launch(detectorThreadContext) {
                    // TODO: still a (very minor) race condition here. what should be changed?
                    val awaited = awaitResponse(response)
                    responseMutex.withLock {
                        if (!successful) {
                            entry.responses.trySend(awaited)
                            if (awaited != null) {
                                successful = true
                                event.run(awaited)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun ExposedModule.onLoad() {}
    override fun ExposedModule.onDisable() {}
    override fun children() = emptyModuleList()

    @ExperimentalCoroutinesApi
    override suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>?) =
        addDetectAndAwait(input, basis) { block ->
            attempt(timeoutDuration, block)
        }

    @ExperimentalCoroutinesApi
    override suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>?, attempts: UInt) =
        addDetectAndAwait(input, basis) { block ->
            attempt(attempts) { block() }
        }

    private suspend inline fun addDetectAndAwait(
        input: T?,
        basis: Listenable<*>?,
        attemptFunc: (block: suspend () -> R?) -> R?
    ): R? {
        return addAndAwait(input, basis ?: trials[0].basis, false, attemptFunc)
    }

    protected suspend inline fun addAndAwait(
        input: T?,
        basis: Listenable<*>,
        isRequest: Boolean,
        attemptFunc: (suspend () -> R?) -> R?
    ): R? {
        val responses = Channel<R?>()
        entries += TrialEntry(isRequest, input, basis, responses)
        val final = attemptFunc {
            withTimeoutOrNull(timeoutDuration) { responses.receive() }
        }
        responses.close()
        return final
    }

    protected abstract fun TrialScope.runTests(supplier: S, entry: TrialEntry<T, *>?): TrialResult<R>?
}

private data class TrialEntry<T : Any, R : Any>(
    val isRequest: Boolean,
    val input: T?,
    val basis: Listenable<*>,
    val responses: Channel<R?>
)

private class TrialDetector<T : Any, R : Any>(
    override val trials: List<DetectorTrial<T, R>>,
    override val timeoutDuration: Duration
) : DetectorDetail<T, R, DetectorTrial.ResultSupplier<T, R>>() {
    override fun TrialScope.runTests(
        supplier: DetectorTrial.ResultSupplier<T, R>,
        entry: TrialEntry<T, *>?
    ): TrialResult<R>? {
        return supplier.supplyIn(this, entry?.input)
    }
}

private class TrialRequester<T : Any, R : Any>(
    private val debugName: String,
    override val trials: List<RequesterTrial<T, R>>,
    override val timeoutDuration: Duration
) : DetectorDetail<T, R, RequesterTrial.ResultSupplier<T, R>>(), Requester<T, R> {
    init {
        require(trials.isNotEmpty())
    }

    override fun TrialScope.runTests(
        supplier: RequesterTrial.ResultSupplier<T, R>,
        entry: TrialEntry<T, *>?
    ): TrialResult<R>? {
        return supplier.supplyIn(this, entry?.input, entry?.isRequest ?: false)
    }

    override suspend fun requestFrom(module: RModule, input: T): R {
        val response = addAndAwait(input, trials[0].basis, true) { block ->
            var started = false
            attempt(timeoutDuration) {
                if (started) block() else {
                    started = true
                    trials[0].start(input) ?: block()
                }
            }
        }
        return response ?: error("Request trial failed for $debugName requester with input $input")
    }
}

private open class SimpleDetectorModule<T : Any, R : Any>(
    private val detail: DetectorDetail<T, R, *>,
    module: RModule
) : DetectorModule<T, R>, RModule by module {
    override val timeoutDuration get() = detail.timeoutDuration
    override val prevResult get() = detail.prevResult

    override fun getNotificationsFrom(module: RModule): Flow<R> {
        module.depend(this)
        return detail.getNotificationsFrom(module)
    }

    @ExperimentalCoroutinesApi
    override suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>?, attempts: UInt): R? {
        module.depend(this)
        return detail.checkNextFrom(module, input, basis, attempts)
    }

    @ExperimentalCoroutinesApi
    override suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>?): R? {
        module.depend(this)
        return detail.detectFrom(module, input, basis)
    }
}

private class SimpleRequesterModule<T : Any, R : Any>(
    private val detail: TrialRequester<T, R>,
    module: RModule
) : SimpleDetectorModule<T, R>(detail, module), RequesterModule<T, R> {
    override suspend fun requestFrom(module: RModule, input: T): R {
        module.depend(this)
        return detail.requestFrom(module, input)
    }
}