package io.github.homchom.recode.init

import io.github.homchom.recode.event.Listener
import io.github.homchom.recode.event.REvent

typealias ModuleAction<T> = T.() -> Unit

interface RModule {
    val dependencies: List<RModule>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun load()

    fun tryLoad() {
        if (!isLoaded) load()
    }

    fun addDependency(module: RModule)

    fun addReference(module: RModule)
    fun removeReference(module: RModule)

    fun <C, R> listenTo(event: REvent<C, R, *>, listener: Listener<C, R>) =
        event.listen { context, result ->
            if (isEnabled) listener(context, result) else result
        }
}

interface StrongModule : RModule {
    fun enable()
    fun disable()
}

sealed class BasicModule(dependencyList: List<RModule>) : RModule {
    final override var isLoaded = false
        private set
    final override var isEnabled = false
        private set

    override val dependencies: List<RModule> get() = _dependencies
    private val _dependencies = dependencyList.toMutableList()

    protected val references = mutableSetOf<RModule>()

    override fun load() {
        check(!isLoaded) { "Module is already loaded" }
        isLoaded = true
    }

    protected fun enableImpl() {
        check(!isEnabled) { "Module is already enabled" }
        tryLoad()
        for (module in dependencies) module.addReference(this)
        isEnabled = true
    }

    protected fun disableImpl() {
        check(isEnabled) { "Module is already disabled" }
        for (module in dependencies) module.removeReference(this)
        isEnabled = false
    }

    protected abstract fun onLoad()
    protected abstract fun onEnable()
    protected abstract fun onDisable()

    override fun addDependency(module: RModule) {
        _dependencies += module
    }

    override fun addReference(module: RModule) {
        if (!isEnabled) enableImpl()
        references += module
    }

    override fun removeReference(module: RModule) {
        references -= module
    }
}

class WeakModule(
    dependencyList: List<RModule>,
    private val onLoad: ModuleAction<RModule>?,
    private val onEnable: ModuleAction<RModule>?,
    private val onDisable: ModuleAction<RModule>?
) : BasicModule(dependencyList) {
    override fun onLoad() {
        onLoad?.invoke(this)
    }

    override fun onEnable() {
        onEnable?.invoke(this)
    }

    override fun onDisable() {
        onDisable?.invoke(this)
    }

    override fun removeReference(module: RModule) {
        super.removeReference(module)
        if (references.isEmpty()) disableImpl()
    }
}

class BasicStrongModule(
    dependencyList: List<RModule>,
    private val onLoad: ModuleAction<StrongModule>?,
    private val onEnable: ModuleAction<StrongModule>?,
    private val onDisable: ModuleAction<StrongModule>?
) : BasicModule(dependencyList), StrongModule {
    override fun enable() = enableImpl()
    override fun disable() = disableImpl()

    override fun onLoad() {
        onLoad?.invoke(this)
    }

    override fun onEnable() {
        onEnable?.invoke(this)
    }

    override fun onDisable() {
        onDisable?.invoke(this)
    }
}