package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ModuleDetail
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.module
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * A [List]-backed implementation of [StateListenable] with a combined notification flow.
 *
 * This class is `open`, with one `open` member: [flatten], the flattening method used to combine context
 * and result flows. Defaults to [merge].
 */
open class GroupListenable<T : Any> private constructor(
    private val events: MutableList<StateListenable<T>>
) : StateListenable<T>, List<StateListenable<T>> by events {
    private val module = module(ModuleDetail.Exposed)

    override val isEnabled by module::isEnabled

    // notifications only = 1, previous only = 2, both = 3
    private val update = AtomicInteger(3)

    final override var notifications = emptyFlow<T>()
        get() {
            if (update.compareAndSet(1, 0) ||
                update.compareAndSet(3, 2))
            {
                field = flatten(events.map { it.notifications })
            }
            return field
        }
        private set

    final override var previous: StateFlow<T?> = MutableStateFlow(null)
        get() {
            if (update.compareAndSet(2, 0) ||
                update.compareAndSet(3, 1))
            {
                field = flatten(events.map { it.previous })
                    .stateIn(module, SharingStarted.Eagerly, null)
            }
            return field
        }
        private set

    constructor() : this(mutableListOf())

    fun <S : StateListenable<T>> add(event: S): S {
        events += event
        module.extend(event)
        return event
    }

    open fun <S> flatten(flows: List<Flow<S>>) = flows.merge()

    override fun extend(vararg parents: RModule) = module.extend(*parents)
}