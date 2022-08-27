package io.github.homchom.recode.init

typealias ModuleBuilderScope = ModuleBuilder.() -> Unit
typealias BuiltModuleAction = BuiltModule.() -> Unit

/**
 * Builds a weak [RModule].
 */
inline fun module(key: SingletonKey? = null, builder: ModuleBuilderScope = {}) =
    ModuleBuilder(key)
        .apply(builder)
        .run { basicWeakModule(dependencies, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [RModule].
 */
inline fun strongModule(key: SingletonKey? = null, builder: ModuleBuilderScope = {}) =
    ModuleBuilder(key)
        .apply(builder)
        .run { basicStrongModule(dependencies, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [RModule] to be enabled by entrypoints.
 */
@OptIn(MutatesModuleState::class)
inline fun entrypointModule(builder: ModuleBuilderScope): ActiveStateModule =
    strongModule {
        onLoad {
            ClientStopEvent.hook { disable() }
        }

        builder()
    }

/**
 * @see module
 * @see strongModule
 */
class ModuleBuilder(key: SingletonKey?) {
    init {
        key?.use()
    }

    val dependencies = mutableListOf<RModule>()

    /**
     * A [ModuleActionBuilder] invoked once, when the module is loaded. Listen to events here.
     */
    val onLoad = ModuleActionBuilder()

    /**
     * A [ModuleActionBuilder] invoked when the module is enabled. Listen to events with [onLoad],
     * not here.
     */
    val onEnable = ModuleActionBuilder()

    /**
     * A [ModuleActionBuilder] invoked when the module is disabled.
     */
    val onDisable = ModuleActionBuilder()

    fun depend(vararg modules: RModule) {
        dependencies.addAll(modules)
    }
}

/**
 * Builds an action to be invoked by a [BuiltModule].
 *
 * @see ModuleBuilder
 */
class ModuleActionBuilder {
    var action: BuiltModuleAction? = null
        private set

    operator fun invoke(block: BuiltModuleAction) {
        action = action?.let { prev ->
            {
                prev()
                block()
            }
        } ?: block
    }
}