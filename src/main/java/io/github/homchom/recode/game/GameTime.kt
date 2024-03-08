package io.github.homchom.recode.game

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.listen
import io.github.homchom.recode.event.listenEach
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile

/**
 * The current client tick.
 *
 * @see waitTicks
 * @see AfterClientTickEvent
 */
val currentTick get() = TickRecorder.currentTick

/**
 * Suspends until [AfterClientTickEvent] runs [ticks] times. Unlike [kotlinx.coroutines.delay], this is
 * event-based and respects tick rates.
 */
suspend fun waitTicks(ticks: Int) = AfterClientTickEvent.notifications.take(ticks).collect()

private object TickRecorder {
    var currentTick = 0L

    private val power = Power()

    init {
        power.listenEach(AfterClientTickEvent) { currentTick++ }
    }
}

class TickCountdown(private val duration: Int, private val onFinish: () -> Unit = {}) {
    val isActive get() = counter > 0

    private var counter = 0
    private val power = Power(startEnabled = true)

    fun wind() {
        val launch = !isActive
        counter = duration
        if (launch) power.listen(AfterClientTickEvent) {
            takeWhile { --counter > 0 }
        }.invokeOnCompletion { exception ->
            counter = 0
            if (exception == null) onFinish()
        }
    }
}