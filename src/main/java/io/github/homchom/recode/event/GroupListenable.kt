package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.ModuleDetail
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

/**
 * A [ModuleDetail] for a group of [StateListenable] objects with a combined notification flow. Events are
 * added with the [add] function. When done, use the detail in a module builder such as [module]; subsequent
 * calls to [add] will not affect the module.
 *
 * This class is `open`, with one `open` member: [flatten], the flattening method used to combine context
 * and result flows. Defaults to [merge].
 *
 * @see add
 */
open class GroupListenable<T : Any> : ModuleDetail<ExposedModule, GroupListenable.Module<T>> {
    private val events = mutableListOf<StateListenable<out T>>()

    fun <S : StateListenable<out T>> add(event: S) = event.also { events += it }

    open fun <S> flatten(flows: List<Flow<S>>) = flows.merge()

    override fun applyTo(module: ExposedModule): Module<T> =
        object : Module<T>, ExposedModule by module {
            private val notifications = flatten(events.map { it.getNotificationsFrom(module) })
            override val previous = flatten(events.map { it.previous })
                .stateIn(module, SharingStarted.Eagerly, null)

            override fun getNotificationsFrom(module: RModule) = notifications
        }

    interface Module<T : Any> : StateListenable<T>, ExposedModule
}