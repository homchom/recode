package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.util.unmodifiable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Builds a weak [ExposedModule].
 */
inline fun buildExposedModule(builder: ModuleBuilderScope = {}) = ModuleBuilder()
    .apply(builder)
    .run { exposedModule(children, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Builds a strong [ExposedModule].
 */
inline fun buildStrongExposedModule(builder: ModuleBuilderScope = {}) = ModuleBuilder()
    .apply(builder)
    .run { strongExposedModule(children, onLoad.action, onEnable.action, onDisable.action) }

/**
 * Constructs a weak [ExposedModule] with children and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun exposedModule(
    children: List<RModule> = emptyList(),
    onLoad: ModuleAction? = null,
    onEnable: ModuleAction? = null,
    onDisable: ModuleAction? = null
): ExposedModule {
    return WeakModule(onLoad, onEnable, onDisable).withChildren(children)
}

/**
 * Constructs a strong [ExposedModule] with children and actions [onLoad], [onEnable], and
 * [onDisable].
 */
fun strongExposedModule(
    children: List<RModule> = emptyList(),
    onLoad: ModuleAction? = null,
    onEnable: ModuleAction? = null,
    onDisable: ModuleAction? = null
): ExposedModule {
    return StrongModule(WeakModule(onLoad, onEnable, onDisable)
        .withChildren(children))
}

private fun <T : RModule> T.withChildren(children: List<RModule>) = apply {
    for (child in children) child.addParent(this)
}

private class WeakModule(
    private val onLoad: ModuleAction?,
    private val onEnable: ModuleAction?,
    private val onDisable: ModuleAction?
) : ExposedModule {
    override var isLoaded = false
        private set
    override var isEnabled = false
        private set

    override val children get() = _children.unmodifiable()
    private val _children = mutableListOf<ExposedModule>()

    val usages: Set<RModule> get() = _usages
    private val _usages = mutableSetOf<RModule>()

    override val coroutineScope get() = scope
    private lateinit var scope: CoroutineScope

    @MutatesModuleState
    override fun load() {
        errorIf(isLoaded) { "loaded" }
        isLoaded = true
        onLoad?.invoke(this)
    }

    @MutatesModuleState
    override fun enable() {
        errorIf(isEnabled) { "enabled" }
        tryLoad()
        for (child in children) child.addUsage(this)
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        isEnabled = true
        onEnable?.invoke(this)
    }

    @MutatesModuleState
    override fun disable() {
        errorIf(!isEnabled) { "disabled" }
        for (child in children) child.removeUsage(this)
        scope.cancel()
        isEnabled = false
        onDisable?.invoke(this)
    }

    private inline fun errorIf(value: Boolean, errorWord: () -> String) =
        check(!value) { "This module is already ${errorWord()}" }

    override fun addChild(module: ExposedModule) {
        _children += module
    }

    override fun addParent(module: RModule) = module.addChild(this)

    @MutatesModuleState
    override fun addUsage(module: ExposedModule) {
        if (!isEnabled) enable()
        _usages += module
    }

    @MutatesModuleState
    override fun removeUsage(module: ExposedModule) {
        _usages -= module
    }
}

private class StrongModule(private val impl: WeakModule) : ExposedModule by impl {
    @MutatesModuleState
    override fun removeUsage(module: ExposedModule) {
        impl.removeUsage(module)
        if (impl.usages.isEmpty()) disable()
    }
}