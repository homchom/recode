package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.RecodeDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Builds an [RModule] of flavor [flavor].
 *
 * @see ModuleFlavor
 */
fun <T : RModule> module(flavor: ModuleFlavor<T>) = flavor.applyTo(AssertiveModule())

/**
 * Builds an [RModule] of flavor [flavor] with subsequent builder [builder].
 *
 * @see ModuleFlavor
 */
fun <T : RModule, R : RModule> module(flavor: ModuleFlavor<T>, builder: ModuleDetail<T, R>) =
    module(flavor + builder)

private class AssertiveModule : ExposedModule {
    override val isEnabled = MutableStateFlow(false)
    private val hasBeenLoaded = AtomicBoolean(false)

    private val loadActions = mutableListOf<ModuleAction>()
    private val enableActions = mutableListOf<ModuleAction>()
    private val disableActions = mutableListOf<ModuleAction>()

    private val parents = mutableSetOf<RModule>()

    private var isAsserted = false

    private var coroutineScope = newCoroutineScope().apply {
        cancel("ExposedModule CoroutineScopes start as cancelled")
    }

    override val coroutineContext get() = coroutineScope.coroutineContext

    private fun newCoroutineScope() = CoroutineScope(RecodeDispatcher + SupervisorJob())

    @OptIn(DelicateCoroutinesApi::class)
    override fun extend(vararg parents: RModule) {
        for (parent in parents) {
            this.parents += parent
            GlobalScope.launch {
                parent.isEnabled.collect { current ->
                    if (current) {
                        enable(false)
                    } else if (!isAsserted && parents.none { it.isEnabled.value }) {
                        disable(false)
                    }
                }
            }
        }
    }

    override fun load() {
        if (!hasBeenLoaded.compareAndSet(false, true)) return
        for (action in loadActions) action()
    }

    override fun assert() = enable(true)
    override fun unassert() = disable(true)

    private fun enable(assertion: Boolean) = synchronized(this) {
        if (!isEnabled.compareAndSet(expect = false, update = true)) return
        load()
        coroutineScope = newCoroutineScope()
        isAsserted = assertion
        for (action in enableActions) action()
    }

    private fun disable(assertion: Boolean) = synchronized(this) {
        if (!isEnabled.compareAndSet(expect = true, update = false)) return
        coroutineScope.cancel("Module disabled")
        isAsserted = !assertion
        for (action in disableActions) action()
    }

    override fun onLoad(action: ModuleAction) {
        loadActions += action
    }

    override fun onEnable(action: ModuleAction) {
        enableActions += action
    }

    override fun onDisable(action: ModuleAction) {
        disableActions += action
    }
}