package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.Hook
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.util.collections.immutable

typealias ModuleBuilderScope = ModuleBuilder.() -> Unit
typealias ModuleAction = ExposedModule.() -> Unit

fun module(vararg details: ModuleDetail): RModule = exposedModule(*details)

fun strongModule(vararg details: ModuleDetail): RModule = strongExposedModule(*details)

/**
 * Builds a *weak* [RModule].
 */
inline fun module(vararg details: ModuleDetail, builder: ModuleBuilderScope) =
    exposedModule(*details, ModuleBuilder(builder))

/**
 * Builds a *strong* [RModule].
 */
inline fun strongModule(vararg details: ModuleDetail, builder: ModuleBuilderScope) =
    strongExposedModule(*details, ModuleBuilder(builder))

/**
 * Builds a *strong* [RModule] to be enabled by entrypoints.
 */
@OptIn(MutatesModuleState::class)
inline fun entrypointModule(builder: ModuleBuilderScope) = strongExposedModule {
    onEnable {
        ClientStopEvent.listenEach { disable() }
    }

    builder()
}

fun emptyModuleList() = emptyList<RModule>()

interface ModuleDetail {
    fun children(): List<RModule>

    fun ExposedModule.onLoad()
    fun ExposedModule.onEnable()
    fun ExposedModule.onDisable()
}

inline fun ModuleBuilder(scope: ModuleBuilderScope) = ModuleBuilder().apply(scope)

/**
 * @see module
 * @see strongModule
 */
class ModuleBuilder : ModuleDetail {
    private val children = mutableListOf<RModule>()

    override fun children() = children.immutable()

    /**
     * A [ModuleActionBuilder] invoked once, when the module is loaded.
     *
     * Hook onto [Hook] events here, but listen for [Listenable] notifications via [onEnable] instead.
     */
    private var loadAction = action()

    /**
     * A [ModuleActionBuilder] invoked when the module is enabled.
     *
     * Listen for [Listenable] notifications here, hook onto [Hook] events via [onLoad] instead.
     */
    private var enableAction = action()

    /**
     * A [ModuleActionBuilder] invoked when the module is disabled.
     */
    private var disableAction = action()

    private fun action(): ModuleAction? = null

    override fun ExposedModule.onLoad() {
        loadAction?.invoke(this)
    }

    override fun ExposedModule.onEnable() {
        enableAction?.invoke(this)
    }

    override fun ExposedModule.onDisable() {
        disableAction?.invoke(this)
    }

    fun onLoad(action: ModuleAction) {
        loadAction = action
    }

    fun onEnable(action: ModuleAction) {
        enableAction = action
    }

    fun onDisable(action: ModuleAction) {
        disableAction = action
    }

    fun depend(vararg modules: RModule) {
        children.addAll(modules)
    }
}