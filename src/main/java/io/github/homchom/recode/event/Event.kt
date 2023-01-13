package io.github.homchom.recode.event

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

private class SharedFlowEvent<T> private constructor(
    private val flow: MutableSharedFlow<T>
) : SharedEvent<T>, Listenable<T> by FlowListenable(flow) {
    constructor() : this(MutableSharedFlow(onBufferOverflow = BufferOverflow.DROP_OLDEST))

    // TODO: remove check()?
    override fun run(context: T) = check(flow.tryEmit(context))
}

private class StateFlowEvent<T> private constructor(
    private val flow: MutableStateFlow<T>
) : StateEvent<T>, StateListenable<T> by StateFlowListenable(flow) {
    constructor(initialValue: T) : this(MutableStateFlow(initialValue))

    override fun run(context: T) = flow.update { context }
}