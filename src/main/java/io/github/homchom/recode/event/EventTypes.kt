package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.event.Event
import kotlin.time.Duration

typealias EventInvoker<T> = (context: T) -> Unit

/**
 * A custom (unbuffered) [ResultListenable] event that can be [run]. Event contexts are transformed into results,
 * the most recent of which is stored in [prevResult].
 */
interface CustomEvent<T, R : Any> : ResultListenable<T, R?> {
    suspend fun run(context: T): R

    /**
     * Runs the event by blocking the current thread.
     *
     * @see run
     */
    fun runBlocking(context: T) = runBlocking { run(context) }
}

/**
 * A custom, buffered [ResultListenable] event that can be [run]. Event contexts are supplied and transformed
 * into results; the event runs asynchronously and caches the result on some interval, and the most recent result
 * is stored in [prevResult].
 *
 * @param I The event's input type, which is mapped to [T] only when needed.
 */
interface BufferedCustomEvent<T, R, I> : ResultListenable<T, R?> {
    suspend fun run(input: I): R

    /**
     * Runs the event by blocking the current thread.
     *
     * @see run
     */
    fun runBlocking(input: I) = runBlocking { run(input) }

    fun stabilize()
}

/**
 * A Fabric [Event] wrapped into a [Listenable].
 */
interface WrappedEvent<T, L> : Listenable<T> {
    val invoker: L
}

// TODO: revisit Detector and Requester interfaces (should more be exposed/documented? less?)

/**
 * A [Listenable] that is run algorithmically, based on another Listenable.
 *
 * @param T The type passed as input to the detector.
 * @param R The detector's event context type for detected results.
 *
 * @property timeoutDuration The maximum duration used in detection functions.
 */
interface Detector<T : Any, R : Any> : ResultListenable<R, R?> {
    val timeoutDuration: Duration

    /**
     * Listens for basis invocations from [module] and returns a [Flow] of results.
     */
    fun detectFrom(module: RModule, input: T?): Flow<R?>
}

/**
 * A [Detector] that can execute code to request a result before detecting it.
 */
interface Requester<T : Any, R : Any> : Detector<T, R> {
    /**
     * The number of active, non-failed requests awaiting a result.
     */
    val activeRequests: Int

    /**
     * Makes a request and detects the first non-null result.
     *
     * @throws RequestTimeoutException if a non-null result is not detected in time
     * (as specified by [timeoutDuration]). Note: This exception is caught by the constructor of [TrialResult]
     * and treated as a failure.
     *
     * @see detectFrom
     */
    suspend fun requestFrom(module: RModule, input: T): R
}

/**
 * @see Detector
 * @see RModule
 */
interface DetectorModule<T : Any, R : Any> : Detector<T, R>, RModule

/**
 * @see Requester
 * @see RModule
 */
interface RequesterModule<T : Any, R : Any> : Requester<T, R>, RModule

class RequestTimeoutException(val input: Any?) : IllegalStateException()