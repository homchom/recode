package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

fun <T> createEvent(): SharedEvent<T> = SharedFlowEvent()

fun <T> createStateEvent(initialValue: T): StateEvent<T> = StateFlowEvent(initialValue)

interface SharedEvent<T> : Listenable<T> {
    fun run(context: T)
}

interface StateEvent<T> : SharedEvent<T>, StateListenable<T>

private class SharedFlowEvent<T> : SharedEvent<T>, Listenable<T> {
    private val flow = MutableSharedFlow<T>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override fun getNotificationsFrom(module: ExposedModule) = flow

    // TODO: remove check()?
    override fun run(context: T) = check(flow.tryEmit(context))
}

private class StateFlowEvent<T>(initialValue: T) : StateEvent<T>, StateListenable<T> {
    private val flow = MutableStateFlow(initialValue)

    override val currentState get() = flow.value

    override fun getNotificationsFrom(module: ExposedModule) = flow

    override fun run(context: T) = flow.update { context }
}