package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.Hook
import io.github.homchom.recode.event.Listenable
import kotlinx.coroutines.flow.onEach

typealias ModuleBuilderScope = ModuleBuilder.() -> Unit
typealias ModuleAction = ExposedModule.() -> Unit

/**
 * Builds a *weak* [RModule].
 */
inline fun module(builder: ModuleBuilderScope): RModule = exposedModule(builder)

/**
 * Builds a *strong* [RModule].
 */
inline fun strongModule(builder: ModuleBuilderScope): RModule = strongExposedModule(builder)

/**
 * Builds a *strong* [RModule] to be enabled by entrypoints.
 */
@OptIn(MutatesModuleState::class)
inline fun entrypointModule(builder: ModuleBuilderScope) = strongExposedModule {
    onEnable {
        ClientStopEvent.listen {
            onEach { disable() }
        }
    }

    builder()
}

/**
 * @see module
 * @see strongModule
 */
class ModuleBuilder {
    val children = mutableListOf<RModule>()

    /**
     * A [ModuleActionBuilder] invoked once, when the module is loaded.
     *
     * Hook onto [Hook] events here, but listen for [Listenable] notifications via [onEnable] instead.
     */
    val onLoad = ModuleActionBuilder()

    /**
     * A [ModuleActionBuilder] invoked when the module is enabled.
     *
     * Listen for [Listenable] notifications here, hook onto [Hook] events via [onLoad] instead.
     */
    val onEnable = ModuleActionBuilder()

    /**
     * A [ModuleActionBuilder] invoked when the module is disabled.
     */
    val onDisable = ModuleActionBuilder()

    fun depend(vararg modules: RModule) {
        children.addAll(modules)
    }
}

/**
 * Builds an action to be invoked by a [ExposedModule].
 *
 * @see ModuleBuilder
 */
class ModuleActionBuilder {
    var action: ModuleAction? = null
        private set

    operator fun invoke(block: ModuleAction) {
        action = action?.let { prev ->
            {
                prev()
                block()
            }
        } ?: block
    }
}