package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.event.Event
import kotlin.time.Duration

typealias EventInvoker<T> = (context: T) -> Unit

/**
 * A custom (unbuffered) [Listenable] event that can be [run]. Event contexts are transformed into "results",
 * which are returned by [run].
 */
interface CustomEvent<T, R> : Listenable<T> {
    suspend fun run(context: T): R

    /**
     * Runs the event by blocking the current thread.
     *
     * @see run
     */
    fun runBlocking(context: T) = runBlocking { run(context) }
}

/**
 * A custom, buffered [Listenable] event that can be [run]. Event contexts are supplied and transformed into
 * "results", which are returned by [run]; the event runs asynchronously and caches the result on some interval.
 *
 * @param I The event's input type, which is mapped to [T] only when needed.
 */
interface BufferedCustomEvent<T, R, I> : Listenable<T> {
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
interface Detector<T : Any, R : Any> : Listenable<R> {
    val timeoutDuration: Duration

    /**
     * Listens for [basis] invocations from [module] until a match is found.
     *
     * @returns The event result, or null if one could not be found in time.
     */
    suspend fun detectFrom(module: RModule, input: T?, basis: Listenable<*>? = null): R?

    /**
     * Listens to the next [basis] invocation from [module] and returns a potential match.
     *
     * @returns The event result, or null if there was not a match.
     */
    suspend fun checkNextFrom(module: RModule, input: T?, basis: Listenable<*>? = null, attempts: UInt = 1u): R?
}

/**
 * A [Detector] that can execute code to request a result before detecting it.
 */
interface Requester<T : Any, R : Any> : Detector<T, R> {
    /**
     * Makes a request and detects the result.
     *
     * @see detectFrom
     */
    suspend fun requestFrom(module: RModule, input: T): R

    /**
     * Makes a request and detects the result only from the next invocation.
     *
     * @see checkNextFrom
     */
    suspend fun requestNextFrom(module: RModule, input: T, attempts: UInt = 1u): R
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