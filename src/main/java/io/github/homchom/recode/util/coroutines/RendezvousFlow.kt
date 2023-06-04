package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// TODO: is there a better way to implement parallel decomposed, suspending event listeners?

/**
 * A derivative of [MutableSharedFlow] that waits for its direct collectors to finish on [emit],
 * allowing for dynamic parallel decomposition.
 */
class RendezvousFlow<T> private constructor(private val flow: MutableSharedFlow<T>) : Flow<T>, FlowCollector<T> {
    val subscriptionCount get() = flow.subscriptionCount

    private val rendezvous = Channel<Unit>()
    private val mutex = Mutex()

    constructor(
        replay: Int = 0,
        extraBufferCapacity: Int = 0,
        onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
    ) : this(MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow))

    override suspend fun emit(value: T) {
        mutex.withLock {
            val numCollectors = subscriptionCount.value
            flow.emit(value)
            repeat(numCollectors) { rendezvous.receive() }
        }
    }

    override suspend fun collect(collector: FlowCollector<T>): Nothing = flow.collect {
        try {
            collector.emit(it)
        } finally {
            rendezvous.send(Unit)
        }
    }
}
