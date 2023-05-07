package io.github.homchom.recode.util.coroutines

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// TODO: is there a better way to implement parallel decomposed, suspending event listeners?

/**
 * A derivative of [MutableSharedFlow] that can wait for collectors to finish on [emit], allowing for dynamic
 * parallel decomposition.
 *
 * @see emitAndAwait
 */
class RendezvousFlow<T> private constructor(
    private val sync: MutableSharedFlow<T>,
    private val async: MutableSharedFlow<T>
) : Flow<T>, FlowCollector<T> {
    val subscriptionCount get() = sync.subscriptionCount

    private val rendezvous = Channel<Unit>()
    private val mutex = Mutex()

    constructor(
        replay: Int = 0,
        extraBufferCapacity: Int = 0,
        onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
    ) : this(
        MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow),
        MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
    )

    override suspend fun emit(value: T) = async.emit(value)

    /**
     * Emits [value] to collectors and waits for them to finish.
     */
    suspend fun emitAndAwait(value: T) = mutex.withLock {
        val numCollectors = subscriptionCount.value
        sync.emit(value)
        repeat(numCollectors) { rendezvous.receive() }
    }

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        coroutineScope {
            launch { async.collect(collector) }
            sync.collect {
                try {
                    collector.emit(it)
                } finally {
                    rendezvous.send(Unit)
                }
            }
        }
    }
}
