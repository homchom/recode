package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.GlobalModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.function.Consumer

interface Listenable<T> {
    val notifications: Flow<T>

    fun listenFrom(module: ExposedModule, block: Flow<T>.() -> Flow<T>) =
        notifications.block().launchIn(module.coroutineScope)

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
value class FlowListenable<T>(override val notifications: Flow<T>) : Listenable<T>

@JvmInline
value class StateFlowListenable<T>(override val notifications: StateFlow<T>) : StateListenable<T> {
    override val currentState get() = notifications.value
}

// TODO: asList or toList?
fun <T> merge(vararg events: Listenable<out T>) = events.asList().merge()

fun <T> List<Listenable<out T>>.merge() = FlowListenable(map { it.notifications }.merge())

fun <T> Listenable<out T>.mergeWith(vararg events: Listenable<out T>) = merge(this, *events)

class GroupListenable<T>(
    private val flattenMethod: (List<Flow<T>>) -> Flow<T> = { it.merge() }
) : Listenable<T> {
    override var notifications = emptyFlow<T>()
        get() {
            if (update) {
                update = false
                field = flattenMethod(flows)
            }
            return field
        }
        private set

    private val flows = mutableListOf<Flow<T>>()
    private var update = false

    fun <S : Listenable<out T>> add(event: S) = event.apply {
        flows.add(notifications)
        update = true
    }
}