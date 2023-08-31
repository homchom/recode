package io.github.homchom.recode.event

import io.github.homchom.recode.Power
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
    private val power = Power()

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
                    .stateIn(power, SharingStarted.Eagerly, null)
            }
            return field
        }
        private set

    constructor() : this(mutableListOf())

    fun <S : StateListenable<T>> add(event: S): S {
        events += event
        power.extend(event)
        return event
    }

    open fun <S> flatten(flows: List<Flow<S>>) = flows.merge()

    override fun use(source: Power) = power.use(source)
}