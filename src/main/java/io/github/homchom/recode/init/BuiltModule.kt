package io.github.homchom.recode.init

import io.github.homchom.recode.util.unmodifiable

/**
 * Constructs a basic weak [RModule] with dependencies and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun basicWeakModule(
    dependencies: List<RModule> = emptyList(),
    onLoad: ModuleAction? = null,
    onEnable: ModuleAction? = null,
    onDisable: ModuleAction? = null
): RModule {
    return BuiltModule(onLoad, onEnable, onDisable).withDependencies(dependencies)
}

/**
 * Constructs a basic strong [RModule] with dependencies and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun basicStrongModule(
    dependencies: List<RModule> = emptyList(),
    onLoad: StrongModuleAction? = null,
    onEnable: StrongModuleAction? = null,
    onDisable: StrongModuleAction? = null
): ActiveStateModule {
    return StrongBuiltModule(BuiltModule(onLoad, onEnable, onDisable)
        .withDependencies(dependencies))
}

private fun <T : RModule> T.withDependencies(dependencies: List<RModule>) = apply {
    for (handle in dependencies) handle.addAsDependency(this)
}

private class BuiltModule(
    private val onLoad: StrongModuleAction?,
    private val onEnable: StrongModuleAction?,
    private val onDisable: StrongModuleAction?
) : ActiveStateModule {
    override var isLoaded = false
        private set
    override var isEnabled = false
        private set

    override val dependencies get() = _dependencies.unmodifiable()
    private val _dependencies = mutableListOf<ActiveStateModule>()

    val usages: Set<ActiveStateModule> get() = _usages
    private val _usages = mutableSetOf<ActiveStateModule>()

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
        isEnabled = true
        onEnable?.invoke(this)
    }

    @MutatesModuleState
    override fun disable() {
        errorIf(!isEnabled) { "disabled" }
        for (module in dependencies) module.removeUsage(this)
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
private value class StrongBuiltModule(private val impl: BuiltModule) : ActiveStateModule by impl {
    @MutatesModuleState
    override fun removeUsage(module: ActiveStateModule) {
        impl.removeUsage(module)
        if (impl.usages.isEmpty()) disable()
    }
}