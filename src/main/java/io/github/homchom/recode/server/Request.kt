package io.github.homchom.recode.server

import io.github.homchom.recode.DEFAULT_TIMEOUT_DURATION
import io.github.homchom.recode.util.Matcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration

/**
 * A [Matcher] for [Request] objects, optionally given input context of type [I].
 */
fun interface RequestMatcher<T, I : Any, R : Any> : Matcher<T, R> {
    fun match(raw: T, requestInput: I?): R?
    override fun match(input: T) = match(input, null)
}

/**
 * A [RequestMatcher] with no input.
 */
fun interface NullaryRequestMatcher<T, R : Any> : RequestMatcher<T, Unit, R> {
    override fun match(input: T): R?
    override fun match(raw: T, requestInput: Unit?) = match(raw)
}

/**
 * An action executed to prompt an event invocation. Scans raw event context for a match and invalidates the event.
 *
 * @param T The raw data type processed by the request.
 * @param I The input type sent by the request.
 * @param R The request response type.
 *
 * @property matcher The [RequestMatcher] associated with this request.
 *
 * @see defineRequest
 */
sealed interface Request<T, I : Any, R : Any> {
    val matcher: RequestMatcher<T, I, R>

    suspend fun request(input: I, timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION): R
    suspend operator fun invoke(input: I, timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION) =
        request(input, timeoutDuration)
}

suspend fun <R : Any> Request<*, Unit, R>.request(timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION) =
    request(Unit, timeoutDuration)

suspend operator fun <R : Any> Request<*, Unit, R>.invoke(timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION) =
    request(timeoutDuration)

fun <I : Any> Request<*, I, *>.requestIn(
    scope: CoroutineScope,
    input: I,
    timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION
): Job {
    return scope.launch { requestIn(scope, input, timeoutDuration) }
}

fun Request<*, Unit, *>.requestIn(scope: CoroutineScope, timeoutDuration: Duration = DEFAULT_TIMEOUT_DURATION) =
        requestIn(scope, Unit, timeoutDuration)

/**
 * An object that provides a delegate to a [Request].
 */
sealed interface RequestProvider<T, I : Any, R : Any> {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, Request<T, I, R>>
}

/**
 * A [RequestMatcher] object that holds a [request].
 *
 * @see Request
 */
interface RequestHolder<T, I : Any, R : Any> : RequestMatcher<T, I, R> {
    val request: Request<T, I, R>

    override fun match(input: T) = request.matcher.match(input)
    override fun match(raw: T, requestInput: I?) = request.matcher.match(raw, requestInput)
}