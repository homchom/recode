package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.PolymorphicModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.util.nullable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration

fun <T : Any, B, R : Any> detector(
    basis: Listenable<B>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    trial: Trial<T, B, R>
): Detector<T, R> {
    return TrialDetector(basis, timeoutDuration, trial)
}

inline fun <T : Any, B, R : Any> requester(
    basis: Listenable<B>,
    crossinline start: TrialStart<T>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    noinline trial: RequesterTrial<T, B, R>
): Requester<T, R> {
    return shortCircuitRequester(basis, { start(it); null }, timeoutDuration, trial)
}

fun <T : Any, B, R : Any> shortCircuitRequester(
    basis: Listenable<B>,
    start: ShortCircuitTrialStart<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    trial: RequesterTrial<T, B, R>
): Requester<T, R> {
    return TrialRequester(basis, start, timeoutDuration, trial)
}

private sealed class DetectorModule<T : Any, B, R : Any>(
    private val basis: Listenable<B>,
    val timeoutDuration: Duration
) : Detector<T, R>, PolymorphicModule(exposedModule()) {
    protected open val event = createEvent<R>()

    override val notifications get() = event.notifications

    private val queue = ConcurrentLinkedQueue<Entry<T, R>>()

    override fun onEnable() {
        basis.listenEach { context ->
            for (entry in queue) {
                val response = nullable {
                    withContext(Dispatchers.IO) {
                        TrialScope(this@nullable).runTrial(entry, context) ?: fail()
                    }
                }
                if (response != null) {
                    entry.response.complete(response)
                    event.run(response)
                    break
                }
            }
        }
    }

    override fun onLoad() {}
    override fun onDisable() {}

    override suspend fun detect(input: T?) = addAndAwait { DetectEntry(input, it) }

    protected suspend inline fun addAndAwait(entry: (CompletableDeferred<R>) -> Entry<T, R>): R? {
        val response = CompletableDeferred<R>()
        queue += entry(response)
        return withTimeoutOrNull(timeoutDuration) { response.await() }
    }

    protected abstract suspend fun TrialScope.runTrial(entry: Entry<T, *>, baseContext: B): R?

    protected sealed interface Entry<T : Any, R : Any> {
        val input: T?
        val response: CompletableDeferred<R>
    }

    private data class DetectEntry<T : Any, R : Any>(
        override val input: T?,
        override val response: CompletableDeferred<R>
    ) : Entry<T, R>
}

private open class TrialDetector<T : Any, B, R : Any>(
    basis: Listenable<B>,
    timeoutDuration: Duration,
    private val trial: Trial<T, B, R>
) : DetectorModule<T, B, R>(basis, timeoutDuration) {
    override suspend fun TrialScope.runTrial(entry: Entry<T, *>, baseContext: B) =
        trial(entry.input, baseContext)
}

private open class TrialRequester<T : Any, B, R : Any>(
    basis: Listenable<B>,
    private val start: ShortCircuitTrialStart<T, R>,
    timeoutDuration: Duration,
    private val trial: RequesterTrial<T, B, R>
) : DetectorModule<T, B, R>(basis, timeoutDuration), Requester<T, R> {
    override suspend fun TrialScope.runTrial(entry: Entry<T, *>, baseContext: B) =
        trial(entry.input, baseContext, entry is RequestEntry)

    override suspend fun request(input: T): R {
        start(input)?.let { return it }
        return addAndAwait { RequestEntry(input, it) } ?: error("Request trial failed")
    }

    private data class RequestEntry<T : Any, R : Any>(
        override val input: T?,
        override val response: CompletableDeferred<R>
    ) : Entry<T, R>
}