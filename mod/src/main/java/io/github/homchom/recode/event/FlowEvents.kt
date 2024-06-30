package io.github.homchom.recode.event

import io.github.homchom.recode.Power
import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.runOnMinecraftThread
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive

/**
 * Creates a [CustomEvent], providing results by transforming context with [resultCapture].
 */
fun <T, R : Any> createEvent(resultCapture: (T) -> R): CustomEvent<T, R> = FlowEvent(resultCapture)

/**
 * Creates a [CustomEvent] with a Unit result.
 */
fun <T> createEvent() = createEvent<T, Unit> {}

private class LazyEventImpl<T, R : Any> : ResultListenable<T, R?> {
    private val power = Power()

    override val notifications: MutableSharedFlow<T>
        get() {
            lazyInit()
            return lazyFlow
        }

    override val previous: MutableStateFlow<R?>
        get() {
            lazyInit()
            return lazyPrevious
        }

    private lateinit var lazyFlow: MutableSharedFlow<T>
    private lateinit var lazyPrevious: MutableStateFlow<R?>

    @OptIn(DelicateCoroutinesApi::class)
    private fun lazyInit() {
        if (::lazyFlow.isInitialized) return

        lazyFlow = MutableSharedFlow(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        lazyPrevious = MutableStateFlow(null)

        var activeBySubscription = false
        lazyFlow.subscriptionCount.onEach { count ->
            if (count == 0) {
                if (activeBySubscription) {
                    power.down()
                    activeBySubscription = false
                }
            } else if (!activeBySubscription) {
                power.up()
                activeBySubscription = true
            }
        }.launchIn(GlobalScope)
    }

    override fun use(source: Power) = power.use(source)
}

private class FlowEvent<T, R : Any>(private val resultCapture: (T) -> R) : CustomEvent<T, R> {
    private val lazyImpl = LazyEventImpl<T, R>()

    override val notifications by lazyImpl::notifications
    override val previous by lazyImpl::previous

    override fun run(context: T) = runOnMinecraftThread {
        notifications.checkEmit(context)
        RecodeDispatcher.expedite() // allow for validation and other state mutation
        resultCapture(context).also(previous::checkEmit)
    }

    override fun use(source: Power) = lazyImpl.use(source)
}

class GroupListenable<T : Any> private constructor(
    private val events: MutableList<StateListenable<T>>
) : StateListenable<T>, List<StateListenable<T>> by events {
    private val groupFlows = LazyEventImpl<T, T>()

    private val power = Power(
        extent = groupFlows,
        onEnable = {
            for (event in events) mergeIn(event)
        }
    )

    override val notifications: Flow<T> by groupFlows::notifications
    override val previous: StateFlow<T?> by groupFlows::previous

    constructor() : this(mutableListOf())

    fun <S : StateListenable<T>> add(event: S): S {
        events += event
        event.use(power)
        if (power.isActive) mergeIn(event)
        return event
    }

    override fun use(source: Power) = power.use(source)

    private fun mergeIn(event: StateListenable<T>) {
        power.listenEach(event, groupFlows.notifications::checkEmit)
        event.previous.onEach(groupFlows.previous::checkEmit)
            .launchIn(power)
    }
}

private fun <E> MutableSharedFlow<E>.checkEmit(value: E) =
    check(tryEmit(value)) { "Event notification collectors should not suspend" }