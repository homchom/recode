package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.GlobalModule
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.util.function.Consumer

sealed interface Listenable<T> {
    val notifications: Flow<T>

    fun listenFrom(module: ExposedModule, block: Flow<T>.() -> Flow<T>) =
        notifications.block().launchIn(module.coroutineScope)

    fun listenEachFrom(module: ExposedModule, action: suspend (T) -> Unit) =
        listenFrom(module) { onEach(action) }

    @Deprecated("Only for use in legacy Java code", ReplaceWith("TODO()"))
    @DelicateCoroutinesApi
    fun register(action: Consumer<T>) = listenEachFrom(GlobalModule) { action.accept(it) }
}

fun <T> createEvent(): SharedEvent<T> = SharedFlowEvent()

fun <T> createStateEvent(initialValue: T): StateEvent<T> = StateFlowEvent(initialValue)

interface SharedEvent<T> : Listenable<T> {
    fun run(context: T)
}

interface StateEvent<T> : SharedEvent<T> {
    val currentState: T
}

private class SharedFlowEvent<T> private constructor(private val flow: MutableSharedFlow<T>) : SharedEvent<T> {
    override val notifications: SharedFlow<T> get() = flow

    constructor() : this(MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST))

    // TODO: remove check()?
    override fun run(context: T) = check(flow.tryEmit(context))
}

private class StateFlowEvent<T> private constructor(private val flow: MutableStateFlow<T>) : StateEvent<T> {
    override val notifications: StateFlow<T> get() = flow

    override val currentState get() = flow.value

    constructor(initialValue: T) : this(MutableStateFlow(initialValue))

    override fun run(context: T) = flow.update { context }
}

fun <T> Listenable<out T>.and(vararg events: Listenable<out T>): Listenable<T> {
    val flows = buildList {
        add(notifications)
        for (event in events) add(event.notifications)
    }
    return EventCollector(flows.merge())
}

private class EventCollector<T>(override val notifications: Flow<T>) : Listenable<T>