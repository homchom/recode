package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.*
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
): DetectorModule<T, R> {
    val detail = TrialDetector(basis, timeoutDuration, trial)
    return SimpleDetectorModule(detail, module(detail))
}

inline fun <T : Any, B, R : Any> requester(
    basis: Listenable<B>,
    crossinline start: TrialStart<T>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    noinline trial: RequesterTrial<T, B, R>
): RequesterModule<T, R> {
    return shortCircuitRequester(basis, { start(it); null }, timeoutDuration, trial)
}

fun <T : Any, B, R : Any> shortCircuitRequester(
    basis: Listenable<B>,
    start: ShortCircuitTrialStart<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    trial: RequesterTrial<T, B, R>
): RequesterModule<T, R> {
    val detail = TrialRequester(basis, start, timeoutDuration, trial)
    return SimpleRequesterModule(detail, module(detail))
}

private sealed class DetectorDetail<T : Any, B, R : Any> : Detector<T, R>, ModuleDetail {
    protected abstract val basis: Listenable<B>
    abstract val timeoutDuration: Duration

    private val event = createEvent<R>()

    private val queue = ConcurrentLinkedQueue<Entry<T, R>>()

    override fun getNotificationsFrom(module: ExposedModule) = event.getNotificationsFrom(module)

    override fun ExposedModule.onEnable() {
        basis.listenEach { context ->
            do {
                val entry: Entry<T, R>? = queue.poll()
                val response = nullable {
                    val scope = TrialScope(this@onEnable, this@nullable)
                    withContext(Dispatchers.IO) { scope.runTrial(entry, context) ?: fail() }
                }
                if (response != null) {
                    entry?.response?.complete(response)
                    event.run(response)
                    break
                }
            } while (queue.isNotEmpty())
        }
    }

    override fun ExposedModule.onLoad() {}
    override fun ExposedModule.onDisable() {}
    override fun children() = emptyModuleList()

    override suspend fun detect(input: T?) = addAndAwait { DetectEntry(input, it) }

    protected suspend inline fun addAndAwait(entry: (CompletableDeferred<R?>) -> Entry<T, R>): R? {
        val response = CompletableDeferred<R?>()
        queue += entry(response)
        return withTimeoutOrNull(timeoutDuration) { response.await() }
    }

    protected abstract suspend fun TrialScope.runTrial(entry: Entry<T, *>?, baseContext: B): R?

    protected sealed interface Entry<T : Any, R : Any> {
        val input: T?
        val response: CompletableDeferred<R?>
    }

    private data class DetectEntry<T : Any, R : Any>(
        override val input: T?,
        override val response: CompletableDeferred<R?>
    ) : Entry<T, R>
}

private class TrialDetector<T : Any, B, R : Any>(
    override val basis: Listenable<B>,
    override val timeoutDuration: Duration,
    private val trial: Trial<T, B, R>
) : DetectorDetail<T, B, R>() {
    override suspend fun TrialScope.runTrial(entry: Entry<T, *>?, baseContext: B) =
        trial(entry?.input, baseContext)
}

private class TrialRequester<T : Any, B, R : Any>(
    override val basis: Listenable<B>,
    private val start: ShortCircuitTrialStart<T, R>,
    override val timeoutDuration: Duration,
    private val trial: RequesterTrial<T, B, R>
) : DetectorDetail<T, B, R>(), Requester<T, R> {
    override suspend fun TrialScope.runTrial(entry: Entry<T, *>?, baseContext: B) =
        trial(entry?.input, baseContext, entry is RequestEntry)

    override suspend fun request(input: T): R {
        start(input)?.let { return it }
        return addAndAwait { RequestEntry(input, it) } ?: error("Request trial failed")
    }

    private data class RequestEntry<T : Any, R : Any>(
        override val input: T?,
        override val response: CompletableDeferred<R?>
    ) : Entry<T, R>
}
private open class SimpleDetectorModule<T : Any, R : Any>(
    private val detail: DetectorDetail<T, *, R>,
    module: RModule
) : DetectorModule<T, R>, Listenable<R> by DependentListenable(detail, module), RModule by module {
    override suspend fun detect(input: T?) = detail.detect(input)
}

private class SimpleRequesterModule<T : Any, R : Any>(
    private val detail: TrialRequester<T, *, R>,
    module: RModule
) : SimpleDetectorModule<T, R>(detail, module), RequesterModule<T, R> {
    override suspend fun request(input: T) = detail.request(input)
}