package io.github.homchom.recode.lifecycle

typealias ModuleBuilderScope = ModuleBuilder.() -> Unit
typealias ModuleAction = ExposedModule.() -> Unit

/**
 * Builds a weak [RModule].
 */
inline fun module(key: SingletonKey? = null, builder: ModuleBuilderScope): RModule =
    buildExposedModule(key, builder)

/**
 * Builds a strong [RModule].
 */
inline fun strongModule(key: SingletonKey? = null, builder: ModuleBuilderScope): RModule =
    buildStrongExposedModule(key, builder)

/**
 * Builds a strong [RModule] to be enabled by entrypoints.
 */
@OptIn(MutatesModuleState::class)
inline fun entrypointModule(builder: ModuleBuilderScope) = buildStrongExposedModule {
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

    val children = mutableListOf<RModule>()

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