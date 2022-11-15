package io.github.homchom.recode.server

import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.lifecycle.GlobalModule
import io.github.homchom.recode.lifecycle.SingletonKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

class Request<T : Any, C>(
    key: SingletonKey,
    event: ValidatedEvent<C>,
    private val executor: () -> Unit,
    test: (C) -> T?
) {
    private val channel: Channel<T>

    init {
        key.use()
        channel = Channel(Channel.CONFLATED)
        event.listenFrom(GlobalModule) { context, result ->
            test(context)?.let {
                runBlocking { channel.send(it) }
                false
            } ?: result
        }
    }

    suspend fun send(): T {
        executor()
        return channel.receive()
    }
}

sealed interface Response<T, E> {
    // TODO: make these value classes in kotlin 1.8
    class Pass<T>(val result: T) : Response<T, Nothing>

    class Fail<E>(val error: E) : Response<Nothing, E>
}