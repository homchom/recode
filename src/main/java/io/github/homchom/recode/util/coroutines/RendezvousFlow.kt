package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.newSingleThreadContext

// TODO: is there a better way to implement parallel decomposed, suspending event listeners?

/**
 * A derivative of [MutableSharedFlow] that waits for collectors to finish on [emit], allowing for dynamic
 * parallel decomposition.
 */
class RendezvousFlow<T>(private val shared: MutableSharedFlow<T>) : Flow<T>, FlowCollector<T> {
    private val rendezvous = Channel<Unit>(Channel.BUFFERED)

    val subscriptionCount get() = shared.subscriptionCount

    constructor(
        replay: Int = 0,
        extraBufferCapacity: Int = 0,
        onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
    ) : this(MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow))

    override suspend fun emit(value: T) {
        val numCollectors = subscriptionCount.value
        shared.emit(value)
        repeat(numCollectors) { rendezvous.receive() }
    }

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        shared.collect {
            try {
                collector.emit(it)
            } finally {
                rendezvous.send(Unit)
            }
        }
    }
}
