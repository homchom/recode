package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.PolymorphicModule
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.util.BreaksControlFlow
import io.github.homchom.recode.util.nullable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(BreaksControlFlow::class)
fun interface Trial<T> {
    fun TrialScope.run(): T
}

fun <T : Any> detector(basis: Listenable<T>, trial: Trial<T>): Detector<T> = TrialDetector(basis, trial)

fun <T : Any> requester(basis: Listenable<T>, trial: Trial<T>): Requester<T> = TrialRequester(basis, trial)

fun <T : Any> stateDetector(initialValue: T, basis: Listenable<T>, trial: Trial<T>): StateDetector<T> =
    object : TrialDetector<T>(basis, trial), StateDetectorEvent<T> {
        override val event = createStateEvent(initialValue)
    }

fun <T : Any> stateRequester(initialValue: T, basis: Listenable<T>, trial: Trial<T>): StateRequester<T> =
    object : TrialRequester<T>(basis, trial), StateRequester<T>, StateDetectorEvent<T> {
        override val event = createStateEvent(initialValue)
    }

interface Detector<T : Any> : Listenable<T>, RModule {
    suspend fun detect(): T?
}

interface StateDetector<T : Any> : Detector<T>, StateListenable<T>

private interface StateDetectorEvent<T : Any> : StateDetector<T> {
    val event: StateEvent<T>
    override val currentState get() = event.currentState
}

interface Requester<T : Any> : Detector<T> {
    suspend fun request(): T
}

interface StateRequester<T : Any> : Requester<T>, StateListenable<T>

private open class TrialDetector<T : Any>(
    private val basis: Listenable<T>,
    private val trial: Trial<T>
) : Detector<T>, PolymorphicModule(exposedModule()) {
    open val event = createEvent<T>()

    override val notifications get() = event.notifications

    override fun onEnable() {
        basis.listenEach { detect()?.let(event::run) }
    }

    override fun onLoad() {}
    override fun onDisable() {}

    override suspend fun detect() = runTrial(false)

    @OptIn(BreaksControlFlow::class)
    protected suspend fun runTrial(isRequest: Boolean) = nullable {
        withContext(Dispatchers.IO) {
            with(trial) { TrialScope(this@nullable, isRequest).run() }
        }
    }
}

private open class TrialRequester<T : Any>(
    basis: Listenable<T>,
    trial: Trial<T>
) : TrialDetector<T>(basis, trial), Requester<T> {
    override suspend fun request() = runTrial(true) ?: error("Trial failed for request")
}