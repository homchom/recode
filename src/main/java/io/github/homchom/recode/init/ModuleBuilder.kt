package io.github.homchom.recode.init

typealias WeakModuleBuilderScope = ModuleBuilder<RModule>.() -> Unit
typealias StrongModuleBuilderScope = ModuleBuilder<StrongModule>.() -> Unit

inline fun <T : RModule, R : RModule> module(
    buildTo: ModuleBuilder<T>.() -> R,
    builder: ModuleBuilder<T>.() -> Unit
): R {
    return ModuleBuilder<T>().apply(builder).buildTo()
}

inline fun weakModule(builder: WeakModuleBuilderScope) =
    module({
        WeakModule(dependencies, onLoad.action, onEnable.action, onDisable.action)
    }, builder)

inline fun strongModule(builder: StrongModuleBuilderScope) =
    module({
        BasicStrongModule(dependencies, onLoad.action, onEnable.action, onDisable.action)
    }, builder)

class ModuleBuilder<T : RModule> {
    val dependencies: List<RModule> get() = _dependencies
    private val _dependencies = mutableListOf<RModule>()

    val onLoad = ModuleActionBuilder<T>()
    val onEnable = ModuleActionBuilder<T>()
    val onDisable = ModuleActionBuilder<T>()

    fun depend(vararg modules: RModule) {
        _dependencies.addAll(modules)
    }
}

class ModuleActionBuilder<T : RModule> {
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