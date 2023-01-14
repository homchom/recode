package io.github.homchom.recode.event

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.time.Duration

typealias Trial<T, B, R> = suspend TrialScope.(input: T?, baseContext: B) -> R?
typealias RequesterTrial<T, B, R> = suspend TrialScope.(input: T?, baseContext: B, isRequest: Boolean) -> R?
typealias NullaryTrial<B, R> = suspend TrialScope.(baseContext: B) -> R?
typealias NullaryRequesterTrial<B, R> = suspend TrialScope.(baseContext: B, isRequest: Boolean) -> R?

typealias TrialStart<T> = suspend (input: T) -> Unit
typealias ShortCircuitTrialStart<T, R> = suspend (input: T) -> R?

interface Detector<T : Any, R : Any> : Listenable<R> {
    suspend fun detect(input: T?): R?
}

interface Requester<T : Any, R : Any> : Detector<T, R> {
    suspend fun request(input: T): R

    fun requestIn(scope: CoroutineScope, input: T) = scope.launch { request(input) }
}

interface DetectorModule<T : Any, R : Any> : Detector<T, R>, RModule

interface RequesterModule<T : Any, R : Any> : Requester<T, R>, RModule

suspend fun <R : Any> Requester<Unit, R>.request() = request(Unit)

fun <R : Any> Requester<Unit, R>.requestIn(scope: CoroutineScope) = requestIn(scope, Unit)

inline fun <B, R : Any> nullaryDetector(
    basis: Listenable<B>,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    crossinline trial: NullaryTrial<B, R>
): DetectorModule<Unit, R> {
    return detector(basis, timeoutDuration) { _, baseContext ->
        trial(baseContext)
    }
}

inline fun <B, R : Any> nullaryRequester(
    basis: Listenable<B>,
    crossinline start: suspend () -> Unit,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION,
    crossinline trial: NullaryRequesterTrial<B, R>
): RequesterModule<Unit, R> {
    return requester(basis, { start() }, timeoutDuration) { _, baseContext, isRequest ->
        trial(baseContext, isRequest)
    }
}

/**
 * A [Detector] with children. When listened to by an [ExposedModule], the children will be implicitly added.
 */
class DependentDetector<T : Any, R : Any>(
    private val delegate: Detector<T, R>,
    vararg children: RModule
) : Detector<T, R> by delegate {
    private val children = children.clone()

    override fun getNotificationsFrom(module: ExposedModule): Flow<R> {
        for (child in children) child.addParent(module)
        return delegate.getNotificationsFrom(module)
    }
}