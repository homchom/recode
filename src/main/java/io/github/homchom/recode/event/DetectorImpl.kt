package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.logError
import io.github.homchom.recode.util.attempt
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
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

    override fun ExposedModule.onEnable() = onEnableImpl(this)

    @OptIn(DelicateCoroutinesApi::class)
    protected fun onEnableImpl(module: ExposedModule) {
        for (trial in trials) trial.supplyResultsFrom(module).listenEachFrom(module) listener@{ supplier ->
            var successful = false

            suspend fun considerEntry(entry: TrialEntry<T, R>?) {
                val coroutineScope = CoroutineScope(module.coroutineContext + Job())
                val result = module.trialScope(coroutineScope) { runTests(supplier, entry) }

                // TODO: bad but temporary
                suspend fun sendIfOpen(element: R?) {
                    try {
                        entry?.responses?.send(element)
                    } catch (e: ClosedSendChannelException) {
                        logError("Trial response channel closed")
                    }
                }

                if (result == null) {
                    sendIfOpen(null)
                    coroutineScope.cancel("No trial job")
                    return
                }

                module.launch {
                    val response = result.await()
                    val run = responseMutex.withLock {
                        if (!successful) sendIfOpen(response)
                        val set = !successful && response != null
                        if (set) successful = true
                        set
                    }
                    if (run) withContext(Dispatchers.Default) { event.run(response!!) }
                    coroutineScope.cancel("Trial job completed")
                }
            }

            if (entries.isEmpty()) {
                considerEntry(null)
                return@listener
            }

            val iterator = entries.iterator()
            for (entry in iterator) {
                if (entry.responses.isClosedForSend) {
                    iterator.remove()
                    continue
                }
                if (entry.basis != trial.basis) continue
                considerEntry(entry)
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
    override val activeRequests get() = _activeRequests.get().toUInt()

    private val _activeRequests = AtomicInteger(0)

    init {
        require(trials.isNotEmpty())
    }

    override fun ExposedModule.onEnable() {
        onEnableImpl(this)
        _activeRequests.set(0)
    }

    override fun TrialScope.runTests(
        supplier: RequesterTrial.ResultSupplier<T, R>,
        entry: TrialEntry<T, *>?
    ): TrialResult<R>? {
        return supplier.supplyIn(this, entry?.input, entry?.isRequest ?: false)
    }

    override suspend fun requestFrom(module: RModule, input: T): R {
        _activeRequests.incrementAndGet()
        return try {
            val response = addAndAwait(input, trials[0].basis, true) { block ->
                var started = false
                attempt(timeoutDuration) {
                    if (started) block() else {
                        started = true
                        trials[0].start(input) ?: block()
                    }
                }
            }
            response ?: throw RequestTrialException(debugName, input)
        } finally {
            _activeRequests.decrementAndGet()
        }
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
    override val activeRequests get() = detail.activeRequests

    override suspend fun requestFrom(module: RModule, input: T): R {
        module.depend(this)
        return detail.requestFrom(module, input)
    }
}

class RequestTrialException(name: String, input: Any?) : IllegalStateException(
    "Request trial failed for $name requester with input $input"
)