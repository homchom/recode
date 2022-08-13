package io.github.homchom.recode.init

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

typealias ModuleBuilderScope = ModuleBuilder<ModuleHandle>.() -> Unit
typealias StrongModuleBuilderScope = ModuleBuilder<RModule>.() -> Unit

/**
 * Builds a weak [RModule].
 */
inline fun weakModule(builder: ModuleBuilderScope) = ModuleBuilder<ModuleHandle>()
    .apply(builder)
    .run { basicWeakModule(dependencies, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [RModule].
 */
inline fun strongModule(builder: StrongModuleBuilderScope) = ModuleBuilder<RModule>()
    .apply(builder)
    .run { basicStrongModule(dependencies, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [RModule] to be enabled by entrypoints.
 */
@OptIn(ModuleActiveState::class)
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
class ModuleBuilder<T : ModuleHandle> {
    val dependencies: List<ModuleHandle> get() = _dependencies
    private val _dependencies = mutableListOf<ModuleHandle>()

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

    fun depend(vararg modules: ModuleHandle) {
        _dependencies.addAll(modules)
    }
}

/**
 * Builds an action to be invoked by an [RModule].
 *
 * @see ModuleBuilder
 */
class ModuleActionBuilder<T : ModuleHandle> {
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