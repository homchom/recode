package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.GlobalModule
import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.function.Consumer

interface Listenable<T> {
    fun getNotificationsFrom(module: ExposedModule): Flow<T>

    fun listenFrom(module: ExposedModule, block: Flow<T>.() -> Flow<T>) =
        getNotificationsFrom(module).block().launchIn(module.coroutineScope)

    fun listenEachFrom(module: ExposedModule, action: suspend (T) -> Unit) =
        listenFrom(module) { onEach(action) }

    @Deprecated("Only for use in legacy Java code", ReplaceWith("TODO()"))
    @DelicateCoroutinesApi
    fun register(action: Consumer<T>) = listenEachFrom(GlobalModule) { action.accept(it) }
}

interface StateListenable<T> : Listenable<T> {
    val currentState: T
}

@JvmInline
value class FlowListenable<T>(private val notifications: Flow<T>) : Listenable<T> {
    override fun getNotificationsFrom(module: ExposedModule) = notifications
}

@JvmInline
value class StateFlowListenable<T>(private val notifications: StateFlow<T>) : StateListenable<T> {
    override val currentState get() = notifications.value

    override fun getNotificationsFrom(module: ExposedModule) = notifications
}

// TODO: asList or toList?
fun <T> merge(vararg events: Listenable<out T>, module: ExposedModule) = events.asList().merge(module)

fun <T> List<Listenable<out T>>.merge(module: ExposedModule) =
    FlowListenable(map { it.getNotificationsFrom(module) }.merge())

fun <T> Listenable<out T>.mergeWith(vararg events: Listenable<out T>, module: ExposedModule) =
    merge(this, *events, module = module)

class GroupListenable<T>(
    private val flattenMethod: (List<Flow<T>>) -> Flow<T> = { it.merge() }
) : Listenable<T> {
    private var notifications = emptyFlow<T>()
        get() {
            if (update) {
                update = false
                field = flattenMethod(flows)
            }
            return field
        }

    private val flows = mutableListOf<Flow<T>>()
    private var update = false

    override fun getNotificationsFrom(module: ExposedModule) = notifications

    fun <S : Listenable<out T>> addFrom(module: ExposedModule, event: S) = event.also {
        flows.add(it.getNotificationsFrom(module))
        update = true
    }
}