package io.github.homchom.recode.init

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

typealias ModuleBuilderScope = ModuleBuilder<RModule>.() -> Unit
typealias StrongModuleBuilderScope = ModuleBuilder<ActiveStateModule>.() -> Unit

/**
 * Builds a weak [RModule].
 */
inline fun weakModule(key: SingletonKey? = null, builder: ModuleBuilderScope) =
    ModuleBuilder<RModule>(key)
        .apply(builder)
        .run { basicWeakModule(dependencies, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [RModule].
 */
inline fun strongModule(key: SingletonKey? = null, builder: StrongModuleBuilderScope) =
    ModuleBuilder<ActiveStateModule>(key)
        .apply(builder)
        .run { basicStrongModule(dependencies, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [RModule] to be enabled by entrypoints.
 */
@OptIn(MutatesModuleState::class)
inline fun entrypointModule(builder: StrongModuleBuilderScope) =
    strongModule {
        onLoad {
            ClientLifecycleEvents.CLIENT_STOPPING.register { disable() }
        }

        builder()
    }

/**
 * @see weakModule
 * @see strongModule
 */
class ModuleBuilder<T : RModule>(key: SingletonKey?) {
    init {
        key?.use()
    }

    val dependencies: List<RModule> get() = _dependencies
    private val _dependencies = mutableListOf<RModule>()

    /**
     * A [ModuleActionBuilder] invoked once, when the module is loaded. Listen to events here.
     */
    val onLoad = ModuleActionBuilder<T>()

    /**
     * A [ModuleActionBuilder] invoked when the module is enabled. Listen to events with [onLoad],
     * not here.
     */
    val onEnable = ModuleActionBuilder<T>()

    /**
     * A [ModuleActionBuilder] invoked when the module is disabled.
     */
    val onDisable = ModuleActionBuilder<T>()

    fun depend(vararg modules: RModule) {
        _dependencies.addAll(modules)
    }
}

/**
 * Builds an action to be invoked by an [RModule].
 *
 * @see ModuleBuilder
 */
class ModuleActionBuilder<T : RModule> {
    var action: (T.() -> Unit)? = null
        private set

    operator fun invoke(block: T.() -> Unit) {
        action = action?.let { prev ->
            {
                prev()
                block()
            }
        } ?: block
    }
}