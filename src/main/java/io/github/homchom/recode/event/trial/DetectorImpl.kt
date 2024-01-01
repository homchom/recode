package io.github.homchom.recode.event.trial

import io.github.homchom.recode.*
import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.ui.sendSystemToast
import io.github.homchom.recode.ui.text.translatedText
import io.github.homchom.recode.util.lib.lazyJob
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.*
import java.util.concurrent.CancellationException
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration

/**
 * Creates a [Detector] that runs via one or more [DetectorTrial] objects.
 *
 * @param name The name of what is being detected (used for debugging purposes).
 */
fun <T, R : Any> detector(
    name: String,
    primaryTrial: DetectorTrial<T, R>,
    vararg secondaryTrials: DetectorTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): Detector<T & Any, R> {
    return TrialDetector(name, arrayOf(primaryTrial, *secondaryTrials), timeoutDuration)
}

/**
 * Creates a [Requester] that runs via one or more [RequesterTrial] objects.
 *
 * @param name The name of what is being detected (used for debugging purposes).
 * @param lifecycle The [Listenable] object that defines the requester's lifecycle. If the event is run during
 * a request, the request is cancelled.
 */
fun <T, R : Any> requester(
    name: String,
    lifecycle: Listenable<*>,
    primaryTrial: RequesterTrial<T, R>,
    vararg secondaryTrials: DetectorTrial<T, R>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): Requester<T & Any, R> {
    return TrialRequester(name, lifecycle, primaryTrial, secondaryTrials, timeoutDuration)
}

private open class TrialDetector<T, R : Any>(
    protected val name: String,
    private val trials: Array<out Trial<T, R>>,
    override val timeoutDuration: Duration
) : Detector<T & Any, R> {
    private val event = createEvent<R, R> { it }

    private val power = Power(
        onEnable = {
            listenToTrials()
        }
    )

    init {
        power.extend(event)
    }

    private val entries by lazy { ConcurrentLinkedDeque<DetectEntry<T, R>>() }

    override val notifications by event::notifications
    override val previous by event::previous

    override fun detect(input: T?, hidden: Boolean) = responseFlow(input, false, hidden)

    protected fun responseFlow(input: T?, isRequest: Boolean, hidden: Boolean) = flow {
        coroutineScope {
            power.up()
            val responses = Channel<R?>(Channel.UNLIMITED)
            try {
                // add entry after all current detection loops
                launch(RecodeDispatcher) {
                    yield()
                    entries += DetectEntry(isRequest, input, responses, hidden)
                }
                while (isActive) emit(responses.receive())
            } finally {
                responses.close()
                power.down()
            }
        }
    }

    // TODO: this is arguably ioc hell. someone with more experience in concurrency should review this
    // TODO: add more comments
    @OptIn(DelicateCoroutinesApi::class)
    private fun Power.listenToTrials() {
        for (trialIndex in trials.indices) trials[trialIndex].results.listenEach { supplier ->
            val successful = AtomicBoolean()
            if (entries.isEmpty()) {
                considerEntry(trialIndex, null, supplier, successful)
            }

            val iterator = entries.iterator()
            for (entry in iterator) {
                if (entry.responses.isClosedForSend) {
                    iterator.remove()
                    continue
                }
                if (successful.get()) return@listenEach // fast path
                considerEntry(trialIndex, entry, supplier, successful)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
    private fun considerEntry(
        trialIndex: Int,
        entry: DetectEntry<T, R>?,
        supplier: Trial.ResultSupplier<T & Any, R>,
        successful: AtomicBoolean
    ) {
        val entryScope = CoroutineScope(power.coroutineContext + lazyJob())

        val result = try {
            val trialScope = TrialScope(entryScope, entry?.hidden ?: false)
            logDebug { "trial $trialIndex started for ${debugString(entry?.input, entry?.hidden)}" }
            supplier.supplyIn(trialScope, entry?.input, entry?.isRequest ?: false)
        } catch (e: TrialScopeException) {
            null
        }

        fun finish(state: String) = entryScope.cancelAndLog(
            "trial $trialIndex $state for ${debugString(entry?.input, entry?.hidden)}"
        )

        if (result == null) {
            finish("ended")
            entry?.responses?.trySend(null)
        } else result.invokeOnCompletion handler@{ exception ->
            val state = when (exception) {
                is CancellationException -> "cancelled"
                null -> "ended"
                else -> "ended with exception $exception"
            }
            finish(state)

            if (exception != null) return@handler
            val awaited = result.getCompleted()
            entry?.responses?.trySend(awaited)

            if (awaited == null || successful.compareAndExchange(false, true)) {
                return@handler
            }
            logDebug("$this succeeded; running with context $awaited")
            event.run(awaited)
        }
    }

    override fun use(source: Power) = power.use(source)

    override fun toString() = "$name detector"

    protected fun debugString(input: T?, hidden: Boolean?): String {
        val hiddenString = if (hidden == true) "hidden " else ""
        val entryString = if (input != null) "explicit entry with input $input" else "default entry"
        return "$this ($hiddenString$entryString)"
    }
}

private data class DetectEntry<T, R : Any>(
    val isRequest: Boolean,
    val input: T?,
    val responses: SendChannel<R?>,
    val hidden: Boolean
)

private class TrialRequester<T, R : Any>(
    name: String,
    private val lifecycle: Listenable<*>,
    primaryTrial: RequesterTrial<T, R>,
    secondaryTrials: Array<out DetectorTrial<T, R>>,
    timeoutDuration: Duration
) : TrialDetector<T, R>(name, arrayOf(primaryTrial, *secondaryTrials), timeoutDuration), Requester<T & Any, R> {
    private val start = primaryTrial.start

    override suspend fun request(input: T & Any, hidden: Boolean) = withContext(NonCancellable) {
        lifecycle.notifications
            .onEach { cancel("${this@TrialRequester} lifecycle ended during a request") }
            .launchIn(this)

        val detectChannel = responseFlow(input, true, hidden)
            .filterNotNull()
            .produceIn(this)

        try {
            val response = start(input) ?: withTimeout(timeoutDuration) { detectChannel.receive() }
            coroutineContext.cancelChildren()
            response
        } catch (timeout: TimeoutCancellationException) {
            mc.sendSystemToast(
                translatedText("multiplayer.recode.request_timeout.toast.title"),
                translatedText("multiplayer.recode.request_timeout.toast")
            )
            logError("${debugString(input, hidden)} timed out after $timeoutDuration")
            throw timeout
        }
    }

    override fun toString() = "$name requester"
}