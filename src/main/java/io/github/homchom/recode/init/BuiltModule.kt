package io.github.homchom.recode.init

/**
 * Constructs a basic weak [RModule] with dependencies and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun basicWeakModule(
    dependencies: List<ModuleHandle> = emptyList(),
    onLoad: ModuleAction? = null,
    onEnable: ModuleAction? = null,
    onDisable: ModuleAction? = null
): ModuleHandle {
    return BuiltModule(onLoad, onEnable, onDisable).withDependencies(dependencies)
}

/**
 * Constructs a basic strong [RModule] with dependencies and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun basicStrongModule(
    dependencies: List<ModuleHandle> = emptyList(),
    onLoad: StrongModuleAction? = null,
    onEnable: StrongModuleAction? = null,
    onDisable: StrongModuleAction? = null
): RModule {
    return StrongBuiltModule(BuiltModule(onLoad, onEnable, onDisable)
        .withDependencies(dependencies))
}

private fun <T : RModule> T.withDependencies(dependencies: List<ModuleHandle>) = apply {
    for (handle in dependencies) handle.addAsDependency(this)
}

private class BuiltModule(
    private val onLoad: StrongModuleAction?,
    private val onEnable: StrongModuleAction?,
    private val onDisable: StrongModuleAction?
) : RModule {
    override var isLoaded = false
        private set
    override var isEnabled = false
        private set

    override val dependencies: List<RModule> get() = _dependencies
    private val _dependencies = mutableListOf<RModule>()

    val usages: Set<RModule> get() = _usages
    private val _usages = mutableSetOf<RModule>()

    @ModuleActiveState
    override fun load() {
        check(!isLoaded) { "Module is already loaded" }
        isLoaded = true
        onLoad?.invoke(this)
    }

    @ModuleActiveState
    override fun enable() {
        check(!isEnabled) { "Module is already enabled" }
        tryLoad()
        for (module in dependencies) module.addUsage(this)
        isEnabled = true
        onEnable?.invoke(this)
    }

    @ModuleActiveState
    override fun disable() {
        check(isEnabled) { "Module is already disabled" }
        for (module in dependencies) module.removeUsage(this)
        isEnabled = false
        onDisable?.invoke(this)
    }

    override fun addDependency(module: RModule) {
        _dependencies += module
    }

    override fun addAsDependency(to: ModuleHandle) {
        to.addDependency(this)
    }

    @ModuleActiveState
    override fun addUsage(module: RModule) {
        if (!isEnabled) enable()
        _usages += module
    }

    @ModuleActiveState
    override fun removeUsage(module: RModule) {
        _usages -= module
    }
}

@JvmInline
private value class StrongBuiltModule(private val impl: BuiltModule) : RModule by impl {
    @ModuleActiveState
    override fun removeUsage(module: RModule) {
        impl.removeUsage(module)
        if (impl.usages.isEmpty()) disable()
    }
}