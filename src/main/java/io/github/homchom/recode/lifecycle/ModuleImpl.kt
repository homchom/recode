package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.logDebug
import io.github.homchom.recode.util.coroutines.cancelAndLog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Builds an [RModule] of flavor [flavor].
 *
 * @param name The module's name (optional, used for debugging purposes).
 *
 * @see ModuleFlavor
 */
fun <T : RModule> module(name: String?, flavor: ModuleFlavor<T>) = flavor.applyTo(AssertiveModule(name))

/**
 * Builds an [RModule] of flavor [flavor] with subsequent builder [builder].
 *
 * @param name The module's name (optional, used for debugging purposes).
 *
 * @see ModuleFlavor
 */
fun <T : RModule, R : RModule> module(name: String?, flavor: ModuleFlavor<T>, builder: ModuleDetail<T, R>) =
    module(name, flavor + builder)

/**
 * Builds a vanilla [RModule].
 *
 * @param name The module's name (used for debugging purposes).
 *
 * @see ModuleFlavor
 * @see ModuleDetail.Vanilla
 */
fun module(name: String) = module(name, ModuleDetail.Vanilla)

private class AssertiveModule(private val name: String?) : ExposedModule {
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
                    } else if (!isAsserted) {
                        tryDisable(false)
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
    override fun unassert() = tryDisable(true)

    private fun enable(assertion: Boolean) = synchronized(this) {
        if (!isEnabled.compareAndSet(expect = false, update = true)) return

        load()
        if (name != null) logDebug("module '$name' enabled")
        coroutineScope = newCoroutineScope()

        isAsserted = assertion
        for (action in enableActions) action()
    }

    private fun tryDisable(assertion: Boolean) = synchronized(this) {
        if (parents.any { it.isEnabled.value }) return
        if (!isEnabled.compareAndSet(expect = true, update = false)) return

        if (name != null) coroutineScope.cancelAndLog("module '$name' disabled")

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