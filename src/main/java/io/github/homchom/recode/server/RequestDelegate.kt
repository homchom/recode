package io.github.homchom.recode.server

import io.github.homchom.recode.event.ValidatedHook
import io.github.homchom.recode.lifecycle.GlobalModule
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration

private val requestPropertyNames = mutableSetOf<String>()

/**
 * Defines and returns a delegate to a [Request].
 *
 * @see RequestProvider
 */
inline fun <T, I : Any, R : Any> defineRequest(
    event: ValidatedHook<T>,
    crossinline executor: suspend (I) -> Unit,
    matcher: RequestMatcher<T, I, R>
): RequestProvider<T, I, R> {
    return defineShortCircuitRequest(event, { executor(it); null }, matcher)
}

/**
 * Defines and returns a delegate to a [Request] without input.
 *
 * @see defineRequest
 */
inline fun <T, R : Any> defineNullaryRequest(
    event: ValidatedHook<T>,
    crossinline executor: suspend () -> Unit,
    matcher: NullaryRequestMatcher<T, R>
): RequestProvider<T, Unit, R> {
    return defineRequest(event, { executor() }, matcher)
}

/**
 * Defines and returns a delegate to a [Request] that is validated before being sent.
 *
 * @param executor If this function object returns a non-null value, that value will be returned as
 * the response, and the request will not be sent.
 *
 * @see defineRequest
 */
fun <T, I : Any, R : Any> defineShortCircuitRequest(
    event: ValidatedHook<T>,
    executor: suspend (I) -> R?,
    matcher: RequestMatcher<T, I, R>
): RequestProvider<T, I, R> {
    return RequestDelegate(event, executor, matcher)
}

private class RequestDelegate<T, I : Any, R : Any>(
    event: ValidatedHook<T>,
    private val executor: suspend (I) -> R?,
    override val matcher: RequestMatcher<T, I, R>
) : Request<T, I, R>,
    RequestProvider<T, I, R>,
    RequestMatcher<T, I, R> by matcher,
    ReadOnlyProperty<Any?, Request<T, I, R>>
{
    private val queue = ConcurrentLinkedQueue<Entry<I, R>>()

    init {
        event.listenFrom(GlobalModule) { context, result ->
            queue.peek()?.let { entry ->
                match(context, entry.input)?.let { response ->
                    entry.response.complete(response)
                    queue.poll()
                    false
                } ?: result
            } ?: result
        }
    }

    override suspend fun request(input: I, timeoutDuration: Duration): R {
        executor(input)?.let { return it }
        val response = CompletableDeferred<R>()
        queue += Entry(input, response)
        return withTimeout(timeoutDuration) { response.await() }
    }

    override fun provideDelegate(thisRef: Any?, property: KProperty<*>) = also {
        val name = property.name
        check (name !in requestPropertyNames) {
            "Two Request properties with name $name were instantiated, but only one is allowed globally"
        }
        requestPropertyNames += name
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = this

    private data class Entry<I, R>(val input: I, val response: CompletableDeferred<R>)
}