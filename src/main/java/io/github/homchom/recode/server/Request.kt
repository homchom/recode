package io.github.homchom.recode.server

import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.init.GlobalModule
import io.github.homchom.recode.init.SingletonKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking

sealed interface Request<T : Any> {
    suspend fun send(): T
}

@Suppress("FunctionName")
fun <T : Any, C> Request(
    key: SingletonKey,
    event: ValidatedEvent<C>,
    executor: () -> Unit,
    test: (C) -> T?
): Request<T> {
    return RequestImpl(key, event, executor, test)
}

private class RequestImpl<T : Any, C>(
    key: SingletonKey,
    event: ValidatedEvent<C>,
    private val executor: () -> Unit,
    test: (C) -> T?
) : Request<T> {
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

    override suspend fun send(): T {
        executor()
        return channel.receive()
    }
}