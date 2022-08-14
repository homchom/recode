package io.github.homchom.recode.init

import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Listener

typealias ModuleAction = ModuleHandle.() -> Unit
typealias StrongModuleAction = RModule.() -> Unit

/**
 * A global handle of an [RModule] without access to mutable active state.
 */
interface ModuleHandle {
    val dependencies: List<ModuleHandle>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun addDependency(module: RModule)
    fun addAsDependency(to: ModuleHandle)

    /**
     * @see Listenable.listenFrom
     */
    fun <C, R> listenTo(event: Listenable<C, R>, explicit: Boolean = true, listener: Listener<C, R>) =
        event.listenFrom(this, explicit, listener)
}

/**
 * A group of code with dependencies and that can be loaded, enabled, and disabled.
 *
 * All modules are either **weak**, meaning they load and unload as needed based on its dependent
 * modules, or **strong**, meaning they remain active even if no dependents are enabled.
 *
 * @see weakModule
 * @see strongModule
 */
interface RModule : ModuleHandle {
    @ModuleActiveState
    fun load()

    @ModuleActiveState
    fun tryLoad() {
        if (!isLoaded) load()
    }

    @ModuleActiveState
    fun enable()

    @ModuleActiveState
    fun disable()

    /**
     * Tells this module that [module] is currently using it.
     */
    @ModuleActiveState
    fun addUsage(module: RModule)

    /**
     * Tells this module that [module] is not currently using it.
     */
    @ModuleActiveState
    fun removeUsage(module: RModule)
}

/**
 * An opt-in annotation denoting that a function or type mutates global active state of an [RModule].
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn("This function mutates global active state of a module and should only be " +
        "used by RModule implementations, with caution")
annotation class ModuleActiveState