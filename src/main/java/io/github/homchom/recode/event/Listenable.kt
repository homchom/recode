package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.CoroutineModule
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.GlobalModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.yield
import java.util.function.Consumer

/**
 * @see ResultListenable
 */
typealias StateListenable<T> = ResultListenable<T, T?>

/**
 * Something that can be listened to. Listenable objects come in two types: *events*, which are run
 * explicitly, and *detectors*, which are run algorithmically (via a [Trial]) based on another Listenable.
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
     * Gets the [Flow] of this object's notifications.
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
 * @property prevResult The result of the previous invocation. For all implementations, it is invariant for this to
 * not update until at least the first suspension point.
 */
interface ResultListenable<T, R> : Listenable<T> {
    val prevResult: R
}

/**
 * A wrapper for a [Flow] into a [Listenable] object.
 */
@JvmInline
value class FlowListenable<T>(private val notifications: Flow<T>) : Listenable<T> {
    override fun getNotificationsFrom(module: RModule) = notifications
}

/**
 * Wraps this [Flow] into a [Listenable].
 */
fun <T> Flow<T>.asListenable() = FlowListenable(this)

/**
 * @param module The module accessing the events.
 *
 * @see [kotlinx.coroutines.flow.merge]
 */
fun <T> merge(vararg events: Listenable<out T>, module: ExposedModule) = events.asList().merge(module)

/**
 * @param module The module accessing the events.
 *
 * @see [kotlinx.coroutines.flow.merge]
 */
fun <T> List<Listenable<out T>>.merge(module: ExposedModule) =
    map { it.getNotificationsFrom(module) }.merge().asListenable()

/**
 * A group of [Listenable] objects with a combined notification flow.
 *
 * @param flattenMethod The flattening method to use for the combined flow.
 *
 * @see merge
 */
class GroupListenable<T : Any>(
    private val flattenMethod: (List<Flow<T>>) -> Flow<T> = { it.merge() }
) : StateListenable<T> {
    override var prevResult: T? = null
        private set

    private var notifications = emptyFlow<T>()
        get() {
            if (update) {
                update = false
                field = flattenMethod(flows).onEach {
                    yield()
                    prevResult = it
                }
            }
            return field
        }

    private val flows = mutableListOf<Flow<T>>()
    private var update = false

    override fun getNotificationsFrom(module: RModule) = notifications

    fun <S : Listenable<out T>> addFrom(module: RModule, event: S) = event.also {
        flows.add(it.getNotificationsFrom(module))
        update = true
    }
}