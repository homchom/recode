package io.github.homchom.recode.event

import io.github.homchom.recode.PowerSink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.fabricmc.fabric.api.event.Event
import java.util.function.Consumer
import kotlin.time.Duration

typealias EventInvoker<T> = (context: T) -> Unit

/**
 * Something that can be listened to. Listenable objects come in two types: *events*, which are run
 * explicitly, and *detectors*, which are run algorithmically (via a [io.github.homchom.recode.event.trial.Trial])
 * based on another Listenable.
 *
 * Listenable is based on the [Flow] API, but the standard [listenEachFrom] method does not allow for
 * suspension. When working with [notifications] and the underlying Flow, collectors generally should
 * not suspend because Listenable implementations should be conflated. Listenable objects are also [PowerSink]s,
 * with a charge equal to the subscriber count of `notifications`.
 *
 * @param T The context type of each invocation. Context includes return values and can therefore be mutated
 * (before the first suspension point). These types are **not** usually thread-safe, so be careful when mutating
 * context concurrently.
 *
 * @see CustomEvent
 * @see WrappedEvent
 * @see Detector
 */
interface Listenable<out T> : PowerSink {
    /**
     * The [Flow] of this object's notifications, evaluated lazily.
     */
    val notifications: Flow<T>

    @Deprecated("Only for use in legacy Java code", ReplaceWith("TODO()"))
    @DelicateCoroutinesApi
    fun register(action: Consumer<in T>) = listenEachFrom(GlobalScope, action::accept)
}

/**
 * Adds a listener, running [block] on the object's notifications.
 *
 * @see Listenable.notifications
 */
fun <T> Listenable<T>.listenFrom(scope: CoroutineScope, block: Flow<T>.() -> Flow<T>) =
    notifications.block().launchIn(scope)

/**
 * Adds a listener, running [action] for each notification.
 *
 * @see listenFrom
 * @see Listenable.notifications
 */
fun <T> Listenable<T>.listenEachFrom(scope: CoroutineScope, action: (T) -> Unit) =
    listenFrom(scope) { onEach(action) }

/**
 * A [Listenable] with a result of type [R].
 *
 * @property previous A [StateFlow] of the previous invocation's result, evaluated lazily with [notifications].
 */
interface ResultListenable<out T, out R> : Listenable<T> {
    val previous: StateFlow<R>
}

/**
 * @see ResultListenable
 */
interface StateListenable<out T : Any> : ResultListenable<T, T?>

/**
 * A custom (unbuffered) [ResultListenable] event that can be [run]. Event contexts are transformed into results,
 * the most recent of which is stored in [previous].
 */
interface CustomEvent<T, R : Any> : ResultListenable<T, R?> {
    fun run(context: T): R
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
interface Detector<T : Any, out R : Any> : StateListenable<R> {
    val timeoutDuration: Duration

    /**
     * Listens for basis invocations and returns a [Flow] of results.
     *
     * @param hidden Tells the detector to invalidate certain "notification-like" intermediate event contexts
     * (see [io.github.homchom.recode.event.trial.TrialScope.hidden]). It is up to each detector to honor this
     * if applicable.
     */
    fun detect(input: T?, hidden: Boolean = false): Flow<R?>
}

/**
 * A [Detector] that can execute code to request a result before detecting it.
 */
interface Requester<T : Any, out R : Any> : Detector<T, R> {
    /**
     * Makes a request and detects the first non-null result.
     *
     * @throws kotlinx.coroutines.TimeoutCancellationException if a non-null result is not detected in time
     * (as specified by [timeoutDuration]).
     *
     * @see detect
     */
    suspend fun request(input: T, hidden: Boolean = false): R
}

/**
 * @throws kotlinx.coroutines.TimeoutCancellationException
 *
 * @see Requester.request
 */
suspend fun <R : Any> Requester<Unit, R>.request(): R = request(Unit)