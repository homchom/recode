package io.github.homchom.recode.init

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents

typealias ReadOnlyModuleBuilderScope = ModuleBuilder<ModuleView>.() -> Unit
typealias ModuleBuilderScope = ModuleBuilder<RModule>.() -> Unit

/**
 * Builds an [RModule] with [builder], returning the output given by [buildTo].
 */
inline fun <T : ModuleView, R : ModuleHandle> module(
    buildTo: ModuleBuilder<T>.() -> R,
    builder: ModuleBuilder<T>.() -> Unit
): R {
    return ModuleBuilder<T>().apply(builder).buildTo()
}

/**
 * Builds a basic weak [RModule].
 */
inline fun weakModule(builder: ReadOnlyModuleBuilderScope) =
    module({
        weakModule(dependencies, onLoad.action, onEnable.action, onDisable.action)
    }, builder)

/**
 * Builds a basic strong [RModule].
 */
inline fun strongModule(builder: ModuleBuilderScope) =
    module({
        strongModule(dependencies, onLoad.action, onEnable.action, onDisable.action)
    }, builder)

/**
 * Builds a strong [RModule] to be enabled by entrypoints.
 */
@OptIn(ModuleActiveState::class)
inline fun entrypointModule(builder: ModuleBuilderScope) =
    strongModule {
        onLoad {
            ClientLifecycleEvents.CLIENT_STOPPING.register { disable() }
        }

        builder()
    }

/**
 * @see module
 */
class ModuleBuilder<T : ModuleView> {
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
class ModuleActionBuilder<T : ModuleView> {
    var action: ModuleAction<T>? = null
        private set

    operator fun invoke(block: ModuleAction<T>) {
        action = action?.let { prev ->
            {
                prev()
                block()
            }
        } ?: block
    }
}