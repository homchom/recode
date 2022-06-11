@file:JvmName("ModuleInit")

package io.github.homchom.recode.init

/**
 * Loads and enables this module.
 */
@ForEntrypointUse
fun EntrypointModule.init() = ModuleLoader.load(this).enable()

private object ModuleLoader : ModuleBuilder {
    private val loaded = mutableMapOf<ModuleDefinition, ModuleImpl>()

    override fun load(module: ModuleDefinition) =
        loaded[module] ?: ModuleImpl(module, this).also { impl ->
            loaded[module] = impl
            with(module) { impl.onLoad() }
        }
}

private class ModuleImpl(
    override val definition: ModuleDefinition,
    val loader: ModuleLoader
) : RModule, ModuleDependency {
    override val dependencies: List<ModuleDependency> get() = _dependencies
    private val _dependencies = mutableListOf<ModuleDependency>()

    override val references get() = _references
    private val _references = mutableListOf<RModule>()

    override var isEnabled = false
        private set

    private var isReferenced = false

    init {
        for (dependency in definition.dependencies) addDependency(dependency)
        loader.setup(this)
    }

    override fun enable() {
        check(!isEnabled) { "Module is already enabled" }
        isEnabled = true
        with(definition) { onEnable() }
        for (dependency in dependencies) dependency.checkReferences()
    }

    override fun disable() {
        check(isEnabled) { "Module is already disabled" }
        isEnabled = false
        with(definition) { onDisable() }
        for (dependency in dependencies) dependency.checkReferences()
    }

    override fun addDependency(dependency: ModuleDefinition) {
        val child = dependency()
        _dependencies += child
        child.addReference(this)
    }

    override fun addReference(reference: RModule) {
        references += reference
        isReferenced = true
    }

    override fun ModuleDefinition.invoke() = loader.load(this)
}