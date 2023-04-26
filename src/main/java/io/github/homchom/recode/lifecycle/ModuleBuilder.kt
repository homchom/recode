package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.util.collections.immutable

typealias ModuleBuilderScope = ModuleBuilder.() -> Unit
typealias ModuleAction = ExposedModule.() -> Unit

/**
 * Builds a *weak* [RModule].
 *
 * @param details [ModuleDetail] components that provide preset implementation to the module.
 */
fun module(vararg details: ModuleDetail): RModule = exposedModule(*details)

/**
 * Builds a *strong* [RModule].
 *
 * @param details [ModuleDetail] objects that provide preset implementation to the module.
 */
fun strongModule(vararg details: ModuleDetail): RModule = strongExposedModule(*details)

/**
 * Builds a *weak* [RModule] with [builder].
 *
 * @param details [ModuleDetail] objects that provide preset implementation to the module.
 */
inline fun module(vararg details: ModuleDetail, builder: ModuleBuilderScope) =
    exposedModule(*details, ModuleBuilder(builder))

/**
 * Builds a *strong* [RModule] with [builder].
 *
 * @param details [ModuleDetail] objects that provide preset implementation to the module.
 */
inline fun strongModule(vararg details: ModuleDetail, builder: ModuleBuilderScope) =
    strongExposedModule(*details, ModuleBuilder(builder))

/**
 * Builds a *strong* [RModule] to be enabled by entrypoints.
 *
 * @see strongModule
 */
@OptIn(MutatesModuleState::class)
inline fun entrypointModule(builder: ModuleBuilderScope) = strongExposedModule {
    onEnable {
        ClientStopEvent.listenEach { disable() }
    }

    builder()
}

fun emptyModuleList() = emptyList<RModule>()

/**
 * A component of an [RModule] that provides preset implementation.
 */
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

    @BuildsRecursiveModule
    override fun ExposedModule.onLoad() {
        loadAction?.invoke(this)
    }

    @BuildsRecursiveModule
    override fun ExposedModule.onEnable() {
        enableAction?.invoke(this)
    }

    @BuildsRecursiveModule
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

@RequiresOptIn("This function of ModuleBuilder invokes from the builder itself. If called from module {}" +
        "or a similar function, this is likely not intended", RequiresOptIn.Level.WARNING)
annotation class BuildsRecursiveModule