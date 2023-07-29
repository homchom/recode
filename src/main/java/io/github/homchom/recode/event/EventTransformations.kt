@file:JvmName("EventTransformations")

package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.RModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

/**
 * A wrapper for a [Flow] into a [Listenable] object.
 */
@JvmInline
value class FlowListenable<T>(private val notifications: Flow<T>) : Listenable<T> {
    override fun getNotificationsFrom(module: RModule) = notifications
}

/**
 * Wraps this [Flow] into a [Listenable].
 */
fun <T> Flow<T>.asListenable() = FlowListenable(this)

/**
 * @see kotlinx.coroutines.flow.transform
 */
inline fun <T, R> Listenable<T>.transform(crossinline transform: suspend FlowCollector<R>.(T) -> Unit) =
    object : Listenable<R> {
        override fun getNotificationsFrom(module: RModule) = flow {
            this@transform.getNotificationsFrom(module).collect { transform(it) }
        }
    }

/**
 * @see kotlinx.coroutines.flow.map
 */
inline fun <T, R> Listenable<T>.map(crossinline transform: (T) -> R) =
    transform { emit(transform(it)) }

/**
 * @see kotlinx.coroutines.flow.filter
 */
inline fun <T> Listenable<T>.filter(crossinline predicate: (T) -> Boolean) =
    transform { if (predicate(it)) emit(it) }

/**
 * @see kotlinx.coroutines.flow.filterIsInstance
 */
inline fun <reified R> Listenable<*>.filterIsInstance() =
    transform { if (it is R) emit(it) }