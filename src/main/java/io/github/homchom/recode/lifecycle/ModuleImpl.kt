package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.RecodeDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Builds an [RModule] of flavor [flavor].
 *
 * @see ModuleBuilder
 * @see ModuleFlavor
 */
fun <T : RModule> module(flavor: ModuleFlavor<T>) = flavor.applyTo(WeakModule())

/**
 * Builds an [RModule] of flavor [flavor] with [builder].
 *
 * @see ModuleBuilder
 * @see ModuleFlavor
 */
fun <T : RModule> module(flavor: ModuleFlavor<T>, builder: ModuleBuilder) = module(builder + flavor)

private class WeakModule : ExposedModule {
    override var hasBeenLoaded = false
        private set
    override var isEnabled = false
        private set

    private val loadActions = mutableListOf<ModuleAction>()
    private val enableActions = mutableListOf<ModuleAction>()
    private val disableActions = mutableListOf<ModuleAction>()

    override val children get() = _children.toSet()
    private val _children = mutableSetOf<ExposedModule>()

    val usages: Set<RModule> get() = _usages
    private val _usages = mutableSetOf<RModule>()

    private var coroutineScope: CoroutineScope? = null

    override val coroutineContext get() = coroutineScope?.coroutineContext
        ?: error("Module is disabled")

    private fun newCoroutineScope() = CoroutineScope(RecodeDispatcher() + SupervisorJob())

    override fun load() {
        if (hasBeenLoaded) return
        hasBeenLoaded = true
        for (action in loadActions) action()
    }

    @OptIn(ModuleUnsafe::class)
    override fun enable() {
        if (isEnabled) return
        load()
        for (child in children) child.addUsage(this)
        coroutineScope = newCoroutineScope()
        isEnabled = true
        for (action in enableActions) action()
    }

    @OptIn(ModuleUnsafe::class)
    override fun disable() {
        if (!isEnabled) return
        for (child in children) child.removeUsage(this)
        coroutineScope!!.cancel("Module disabled")
        coroutineScope = null
        isEnabled = false
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

    @OptIn(ModuleUnsafe::class)
    override fun addChild(module: ExposedModule) {
        check(module !in children) { "Module already has this child" }
        _children += module
        if (isEnabled) module.addUsage(this)
    }

    override fun equals(other: Any?) = super.equals(other)

    override fun hashCode() = super.hashCode()

    @OptIn(ModuleUnsafe::class)
    override fun extend(vararg parents: RModule) {
        for (parent in parents) {
            if (this !in parent.children) parent.addChild(this)
        }
    }

    @ModuleUnsafe
    override fun addUsage(module: ExposedModule) {
        if (!isEnabled) enable()
        _usages += module
    }

    @ModuleUnsafe
    override fun removeUsage(module: ExposedModule) {
        _usages -= module
        if (usages.isEmpty()) disable()
    }
}