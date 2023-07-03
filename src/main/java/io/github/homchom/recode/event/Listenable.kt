package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.*
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
 * @property previous A [StateFlow] of the previous invocations' results.
 */
interface ResultListenable<T, R> : Listenable<T> {
    val previous: StateFlow<R>
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
 * A [ModuleDetail] for a group of [StateListenable] objects with a combined notification flow. Events are
 * added with the [add] function. When done, use the detail in a module builder such as [module]; subsequent
 * calls to [add] will not affect the module.
 *
 * This class is `open`, with one `open` member: [flatten], the flattening method used to combine context
 * and result flows. Defaults to [merge].
 *
 * @see add
 */
open class GroupListenable<T : Any> : ModuleDetail<ExposedModule, GroupListenable.Module<T>> {
    private val events = mutableListOf<StateListenable<out T>>()

    fun <S : StateListenable<out T>> add(event: S) = event.also { events += it }

    open fun <S> flatten(flows: List<Flow<S>>) = flows.merge()

    override fun applyTo(module: ExposedModule): Module<T> =
        object : Module<T>, ExposedModule by module {
            private val notifications = flatten(events.map { it.getNotificationsFrom(module) })
            override val previous = flatten(events.map { it.previous })
                .stateIn(module, SharingStarted.Eagerly, null)

            override fun getNotificationsFrom(module: RModule) = notifications
        }

    interface Module<T : Any> : StateListenable<T>, ExposedModule
}