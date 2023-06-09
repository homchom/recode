package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.RecodeDispatcher
import io.github.homchom.recode.util.collections.ImmutableList
import io.github.homchom.recode.util.collections.immutable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

fun exposedModule(vararg details: ModuleDetail): ExposedModule =
    createModule(details) { WeakModule(UsageModule(it)) }

fun strongExposedModule(vararg details: ModuleDetail): ExposedModule =
    createModule(details) { UsageModule(it) }

/**
 * Builds a *weak* [ExposedModule].
 */
inline fun exposedModule(vararg details: ModuleDetail, builder: ModuleBuilderScope) =
    exposedModule(*details, ModuleBuilder(builder))

/**
 * Builds a *strong* [ExposedModule].
 */
inline fun strongExposedModule(vararg details: ModuleDetail, builder: ModuleBuilderScope) =
    strongExposedModule(*details, ModuleBuilder(builder))

private inline fun createModule(
    details: Array<out ModuleDetail>,
    constructor: (ImmutableList<ModuleDetail>) -> ExposedModule
): ExposedModule {
    val detailList = details.toList().immutable()
    return constructor(detailList).apply {
        for (detail in detailList) {
            for (child in detail.children()) depend(child)
        }
    }
}

private class UsageModule(private val details: ImmutableList<ModuleDetail>) : ExposedModule {
    override var isLoaded = false
        private set
    override var isEnabled = false
        private set

    override val children get() = _children.immutable()
    private val _children = mutableSetOf<ExposedModule>()

    val usages: Set<RModule> get() = _usages
    private val _usages = mutableSetOf<RModule>()

    private var coroutineScope = newCoroutineScope()

    override val coroutineContext get() = coroutineScope.coroutineContext

    private fun newCoroutineScope() = CoroutineScope(RecodeDispatcher() + SupervisorJob())

    @MutatesModuleState
    override fun load() {
        errorIf(isLoaded) { "is loaded" }
        isLoaded = true
        forEachDetail { onLoad() }
    }

    @MutatesModuleState
    override fun enable() {
        errorIf(isEnabled) { "is enabled" }
        tryLoad()
        for (child in children) child.addUsage(this)
        isEnabled = true
        forEachDetail { onEnable() }
    }

    @MutatesModuleState
    override fun disable() {
        errorIf(!isEnabled) { "is disabled" }
        for (child in children) child.removeUsage(this)
        coroutineScope.cancel("Module disabled")
        coroutineScope = newCoroutineScope()
        isEnabled = false
        forEachDetail { onDisable() }
    }

    private inline fun errorIf(value: Boolean, errorWord: () -> String) =
        check(!value) { "This module already ${errorWord()}" }

    @OptIn(MutatesModuleState::class)
    override fun addChild(module: ExposedModule) {
        errorIf(module in children) { "has this child" }
        _children += module
        if (isEnabled) module.addUsage(this)
    }

    override fun equals(other: Any?) = super.equals(other)

    override fun hashCode() = super.hashCode()

    override fun addParent(module: RModule) {
        module.addChild(this)
    }

    @MutatesModuleState
    override fun addUsage(module: ExposedModule) {
        if (!isEnabled) enable()
        _usages += module
    }

    @MutatesModuleState
    override fun removeUsage(module: ExposedModule) {
        _usages -= module
    }

    private inline fun forEachDetail(block: (ModuleDetail).() -> Unit) {
        for (detail in details) detail.block()
    }
}

private class WeakModule(private val impl: UsageModule) : ExposedModule by impl {
    @MutatesModuleState
    override fun removeUsage(module: ExposedModule) {
        impl.removeUsage(module)
        if (impl.usages.isEmpty()) disable()
    }
}