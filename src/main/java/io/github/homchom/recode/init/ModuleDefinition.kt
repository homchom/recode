package io.github.homchom.recode.init

/**
 * Defines a module, a group of code that can be loaded, enabled, and disabled.
 *
 * @property isPersistent Whether the module remains enabled if all modules that
 * depend on it are disabled.
 * @property dependencies All modules that depend on this one.
 */
interface ModuleDefinition {
    val isPersistent get() = false

    val dependencies: List<ModuleDefinition>

    /**
     * Runs once, after the module is first loaded. Add event listeners here.
     */
    fun RModule.onLoad()

    /**
     * Runs each time the module is enabled. Add event listeners to [onLoad], not here.
     */
    fun RModule.onEnable()

    /**
     * Runs each time the module is disabled.
     */
    fun RModule.onDisable()

    fun none() = emptyList<ModuleDefinition>()
}

/**
 * Defines a module used by mod entrypoints.
 *
 * @see ModuleDefinition
 */
interface EntrypointModule : ModuleDefinition {
    override val isPersistent get() = true

    fun RModule.onInit()
    fun RModule.onClose()

    override fun RModule.onLoad() = onInit()
    override fun RModule.onEnable() = Unit
    override fun RModule.onDisable() = onClose()
}

@RequiresOptIn("This should only be used by entrypoints")
annotation class ForEntrypointUse