@file:JvmName("EventTransformations")

package io.github.homchom.recode.event

import io.github.homchom.recode.Power
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

/**
 * @see kotlinx.coroutines.flow.transform
 */
inline fun <T, R> Listenable<T>.transform(crossinline transform: suspend FlowCollector<R>.(T) -> Unit) =
    object : Listenable<R> {
        override val notifications = flow {
            this@transform.notifications.collect { transform(it) }
        }

        override fun use(source: Power) = this@transform.use(source)
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

/**
 * @see merge
 */
fun <T> merge(vararg events: Listenable<T>) = events.asList().merge()

/**
 * @see Iterable.merge
 */
fun <T> Iterable<Listenable<T>>.merge() = object : Listenable<T> {
    override val notifications = this@merge.map { it.notifications }.merge()

    override fun use(source: Power) {
        for (event in this@merge) event.use(source)
    }
}