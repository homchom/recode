package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.CoroutineModule
import io.github.homchom.recode.lifecycle.GlobalModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.fabricmc.fabric.api.event.Event
import java.util.function.Consumer
import kotlin.time.Duration

/**
 * @see ResultListenable
 */
typealias StateListenable<T> = ResultListenable<T, T?>

typealias EventInvoker<T> = (context: T) -> Unit

/**
 * Something that can be listened to. Listenable objects come in two types: *events*, which are run
 * explicitly, and *detectors*, which are run algorithmically (via a [io.github.homchom.recode.event.trial.Trial])
 * based on another Listenable.
 *
 * Listenable is based on the [Flow] API, but the standard [listenEachFrom] method does not allow for
 * suspension. When working with [getNotificationsFrom] and the underlying Flow, collectors generally should
 * not suspend because Listenable implementations should be conflated.
 *
 * @param T The context type of each invocation. Context includes return values and can therefore be mutated
 * (before the first suspension point). These types are **not** usually thread-safe, so be careful when mutating
 * context concurrently.
 *
 * @see CustomEvent
 * @see WrappedEvent
 * @see Detector
 */
interface Listenable<T> {
    /**
     * A coupled [RModule] that is enabled if (and only if) any listeners are running.
     */
    val dependency: RModule

    /**
     * Gets the [Flow] of this object's notifications.
     *
     * Implementations of this **must** obey the invariant that [dependency] is proper.
     *
     * @param module The module accessing the flow.
     */
    fun getNotificationsFrom(module: RModule): Flow<T>

    /**
     * Adds a listener, running [block] on the object's notifications.
     *
     * @see getNotificationsFrom
     */
    fun listenFrom(module: CoroutineModule, block: Flow<T>.() -> Flow<T>) =
        getNotificationsFrom(module).block().launchIn(module)

    /**
     * Adds a listener, running [action] for each notification.
     *
     * @see listenFrom
     * @see getNotificationsFrom
     */
    fun listenEachFrom(module: CoroutineModule, action: (T) -> Unit) =
        listenFrom(module) { onEach(action) }

    @Deprecated("Only for use in legacy Java code", ReplaceWith("TODO()"))
    @DelicateCoroutinesApi
    fun register(action: Consumer<T>) = listenEachFrom(GlobalModule) { action.accept(it) }
}

/**
 * A [Listenable] with a result of type [R].
 *
 * @property previous A [StateFlow] of the previous invocations' results.
 */
interface ResultListenable<T, R> : Listenable<T> {
    val previous: StateFlow<R>
}

/**
 * A custom (unbuffered) [ResultListenable] event that can be [run]. Event contexts are transformed into results,
 * the most recent of which is stored in [previous].
 */
interface CustomEvent<T, R : Any> : ResultListenable<T, R?> {
    fun run(context: T): R
}

/**
 * A custom, buffered [ResultListenable] event that can be [run]. Event contexts are supplied and transformed
 * into results; the event runs asynchronously and caches the result on some interval, and the most recent result
 * is stored in [previous].
 *
 * @param I The event's input type, which is mapped to [T] only when needed.
 */
interface BufferedCustomEvent<T, R, I> : ResultListenable<T, R?> {
    fun run(input: I): R

    fun stabilize()
}

/**
 * A Fabric [Event] wrapped into a [Listenable].
 */
interface WrappedEvent<T, L> : Listenable<T> {
    val invoker: L
}

/**
 * A [Listenable] that is run algorithmically, based on another Listenable. The standard implementation of
 * this interface is created with the [io.github.homchom.recode.event.trial.detector] function, backed by the
 * [io.github.homchom.recode.event.trial.Trial] API.
 *
 * @param T The type passed as input to the detector.
 * @param R The detector's event context type for detected results.
 *
 * @property timeoutDuration The maximum duration used in detection functions.
 */
interface Detector<T : Any, R : Any> : StateListenable<R> {
    val timeoutDuration: Duration

    /**
     * Listens for basis invocations from [module] and returns a [Flow] of results.
     *
     * @param hidden Tells the detector to invalidate certain "notification-like" intermediate event contexts
     * (see [io.github.homchom.recode.event.trial.TrialScope.hidden]). It is up to each detector to honor this
     * if applicable.
     */
    fun detectFrom(module: RModule, input: T?, hidden: Boolean = false): Flow<R?>
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
     * @throws kotlinx.coroutines.TimeoutCancellationException if a non-null result is not detected in time
     * (as specified by [timeoutDuration]).
     *
     * @see detectFrom
     */
    suspend fun requestFrom(module: RModule, input: T, hidden: Boolean = false): R
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
interface RequesterModule<T : Any, R : Any> : DetectorModule<T, R>, Requester<T, R>