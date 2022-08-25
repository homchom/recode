package io.github.homchom.recode.init

import io.github.homchom.recode.util.unmodifiable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

/**
 * Constructs a basic weak [RModule] with dependencies and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun basicWeakModule(
    dependencies: List<RModule> = emptyList(),
    onLoad: BuiltModuleAction? = null,
    onEnable: BuiltModuleAction? = null,
    onDisable: BuiltModuleAction? = null
): BuiltModule {
    return WeakBuiltModule(onLoad, onEnable, onDisable).withDependencies(dependencies)
}

/**
 * Constructs a basic strong [RModule] with dependencies and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun basicStrongModule(
    dependencies: List<RModule> = emptyList(),
    onLoad: BuiltModuleAction? = null,
    onEnable: BuiltModuleAction? = null,
    onDisable: BuiltModuleAction? = null
): BuiltModule {
    return StrongBuiltModule(WeakBuiltModule(onLoad, onEnable, onDisable)
        .withDependencies(dependencies))
}

private fun <T : RModule> T.withDependencies(dependencies: List<RModule>) = apply {
    for (handle in dependencies) handle.addAsDependency(this)
}

interface BuiltModule : ListenableModule, CoroutineModule, ActiveStateModule

private class WeakBuiltModule(
    private val onLoad: BuiltModuleAction?,
    private val onEnable: BuiltModuleAction?,
    private val onDisable: BuiltModuleAction?
) : BuiltModule {
    override var isLoaded = false
        private set
    override var isEnabled = false
        private set

    override val dependencies get() = _dependencies.unmodifiable()
    private val _dependencies = mutableListOf<ActiveStateModule>()

    val usages: Set<ActiveStateModule> get() = _usages
    private val _usages = mutableSetOf<ActiveStateModule>()

    override val coroutineScope get() = scope
    private lateinit var scope: CoroutineScope

    @MutatesModuleState
    override fun load() {
        errorIf(isLoaded) { "loaded" }
        isLoaded = true
        onLoad?.invoke(this)
    }

    @MutatesModuleState
    override fun enable() {
        errorIf(isEnabled) { "enabled" }
        tryLoad()
        for (module in dependencies) module.addUsage(this)
        scope = CoroutineScope(Dispatchers.Default)
        isEnabled = true
        onEnable?.invoke(this)
    }

    @MutatesModuleState
    override fun disable() {
        errorIf(!isEnabled) { "disabled" }
        for (module in dependencies) module.removeUsage(this)
        scope.cancel()
        isEnabled = false
        onDisable?.invoke(this)
    }

    private inline fun errorIf(value: Boolean, errorWord: () -> String) =
        check(!value) { "This module is already ${errorWord()}" }

    override fun addDependency(module: ActiveStateModule) {
        _dependencies += module
    }

    override fun addAsDependency(to: RModule) {
        to.addDependency(this)
    }

    @MutatesModuleState
    override fun addUsage(module: ActiveStateModule) {
        if (!isEnabled) enable()
        _usages += module
    }

    @MutatesModuleState
    override fun removeUsage(module: ActiveStateModule) {
        _usages -= module
    }
}

@JvmInline
private value class StrongBuiltModule(private val impl: WeakBuiltModule) : BuiltModule by impl {
    @MutatesModuleState
    override fun removeUsage(module: ActiveStateModule) {
        impl.removeUsage(module)
        if (impl.usages.isEmpty()) disable()
    }
}