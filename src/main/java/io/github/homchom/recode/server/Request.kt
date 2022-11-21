package io.github.homchom.recode.server

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.lifecycle.GlobalModule
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private val requestPropertyNames = mutableSetOf<String>()

/**
 * Defines and returns a delegate to a [Request].
 *
 * @param I The Request input type.
 * @param R The Request result type.
 * @param C The Event context type.
 */
fun <I, R : Any, C> defineRequest(
    event: ValidatedEvent<C>,
    executor: (I) -> Unit,
    test: (C) -> R?
): ReadOnlyProperty<Any?, Request<I, R>> {
    return RequestDelegate(event, executor, test)
}

/**
 * Defines and returns a delegate to a [Request] with no input.
 *
 * @see defineRequest
 */
fun <R : Any, C> defineNullaryRequest(event: ValidatedEvent<C>, executor: () -> Unit, test: (C) -> R?) =
    defineRequest<Nothing?, _, _>(event, { executor() }, test)

/**
 * An action executed to prompt an event invocation. Requests are sent with [send].
 *
 * @param I The input type sent by the request.
 * @param R The request result type.
 *
 * @see defineRequest
 */
sealed interface Request<I, R : Any> {
    suspend fun send(context: I, timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION): R
}

suspend fun <R : Any> Request<Nothing?, R>.send(timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION) =
    send(null, timeoutDuration)

private class RequestDelegate<I, R : Any, C>(
    event: ValidatedEvent<C>,
    private val executor: (I) -> Unit,
    test: (C) -> R?
) : Request<I, R>, ReadOnlyProperty<Any?, Request<I, R>> {
    private val channel = Channel<R>(Channel.CONFLATED)

    init {
        event.listenFrom(GlobalModule) { context, result ->
            test(context)?.let {
                runBlocking { channel.send(it) }
                false
            } ?: result
        }
    }

    override suspend fun send(context: I, timeoutDuration: Duration): R {
        executor(context)
        return withTimeout(2.minutes) { channel.receive() }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = this

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) = also {
        val name = property.name
        check (name !in requestPropertyNames) {
            "Two Request properties with name $name were instantiated, but only one is allowed globally"
        }
        requestPropertyNames += name
    }
}



/*
sealed interface Response<T, E> {
    // TODO: make these value classes in kotlin 1.8
    class Pass<T>(val result: T) : Response<T, Nothing>

    class Fail<E>(val error: E) : Response<Nothing, E>
}
*/