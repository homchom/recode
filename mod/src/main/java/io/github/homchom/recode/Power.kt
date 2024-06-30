package io.github.homchom.recode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

typealias PowerCallback = Power.() -> Unit

/**
 * Something that uses [Power].
 */
interface PowerSink {
    fun use(source: Power)
}

/**
 * A lifecycle object, and one of recode's core classes. Use as a private member of other classes to modularize
 * specific code such that resources and computation only exist when needed by other use sites. Such classes can
 * also implement [PowerSink] if their power usage varies externally.
 *
 * @param extent The primary [PowerSink] for which the power is used.
 */
class Power(
    extent: PowerSink? = null,
    private val onEnable: PowerCallback? = null,
    private val onDisable: PowerCallback? = null,
    startEnabled: Boolean = false
) : PowerSink, CoroutineScope {
    private var charge = 0

    private val sources = mutableListOf<Power>()

    private var coroutineScope = newCoroutineScope().apply {
        cancel("Power CoroutineScopes start as cancelled")
    }

    private val mutex = Mutex()

    override val coroutineContext get() = coroutineScope.coroutineContext

    init {
        extent?.use(this)
        if (startEnabled) runBlocking { up() }
    }

    suspend fun up() = updateCharge { it + 1 }
    suspend fun down() = updateCharge { it - 1 }

    private suspend inline fun updateCharge(setter: (Int) -> Int) = mutex.withLock {
        require(charge >= 0) { "power charge cannot be negative" }
        val previous = charge
        charge = setter(charge)
        if (previous == 0 && charge != 0) enable()
        if (previous != 0 && charge == 0) disable()
    }

    override fun use(source: Power) {
        sources += source
    }

    private fun newCoroutineScope() = CoroutineScope(RecodeDispatcher + SupervisorJob())

    private suspend fun enable() {
        coroutineScope = newCoroutineScope()
        for (power in sources) power.up()
        onEnable?.invoke(this)
    }

    private suspend fun disable() {
        coroutineScope.cancel("Power off")
        for (power in sources) power.down()
        onDisable?.invoke(this)
    }
}