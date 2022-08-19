package io.github.homchom.recode.init

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Listener

typealias ModuleAction = RModule.() -> Unit
typealias StrongModuleAction = ActiveStateModule.() -> Unit

/**
 * A group of code with dependencies and that can be loaded, enabled, and disabled.
 *
 * All modules are either **weak**, meaning they load and unload as needed based on its dependent
 * modules, or **strong**, meaning they remain active even if no dependents are enabled.
 *
 * @see weakModule
 * @see strongModule
 */
interface RModule {
    val dependencies: List<RModule>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun addDependency(module: ActiveStateModule)
    fun addAsDependency(to: RModule)

    /**
     * @see Listenable.listenFrom
     */
    fun <C, R> listenTo(event: Listenable<C, R>, listener: Listener<C, R>) =
        event.listenFrom(this, listener)
}

/**
 * An [RModule] with exposed functions for managing active state.
 */
interface ActiveStateModule : RModule {
    @MutatesModuleState
    fun load()

    @MutatesModuleState
    fun tryLoad() {
        if (!isLoaded) load()
    }

    @MutatesModuleState
    fun enable()

    @MutatesModuleState
    fun disable()

    /**
     * Tells this module that [module] is currently using it.
     */
    @MutatesModuleState
    fun addUsage(module: ActiveStateModule)

    /**
     * Tells this module that [module] is not currently using it.
     */
    @MutatesModuleState
    fun removeUsage(module: ActiveStateModule)
}

/**
 * An opt-in annotation denoting that something mutates global active state of an [RModule].
 */
@RequiresOptIn("This mutates global active state of a module and should only be " +
        "used by RModule implementations, with caution")
annotation class MutatesModuleState