package io.github.homchom.recode.init

import io.github.homchom.recode.event.Listener
import io.github.homchom.recode.event.ModuleListenable

/**
 * A view of a module without mutable active state, used by [RModule] to restrict its access.
 */
interface ModuleView {
    val dependencies: List<ModuleView>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun addDependency(module: RModule)

    fun <C, R> listenTo(event: ModuleListenable<C, R>, listener: Listener<C, R>) =
        event.listenFrom(this, listener)
}

/**
 * A group of code with dependencies and that can be loaded, enabled, and disabled.
 *
 * All modules are either **weak**, meaning they load and unloaded as needed based on its dependent
 * modules, or **strong**, meaning they remain active even if no dependents are enabled.
 *
 * @see module
 * @see weakModule
 * @see strongModule
 */
interface RModule : ModuleView {
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

    @ModuleActiveState
    fun addUsage(module: RModule)

    @ModuleActiveState
    fun removeUsage(module: RModule)
}

/**
 * A global handle of an [RModule], used for adding it as dependencies to other modules.
 */
interface ModuleHandle {
    fun addAsDependency(to: ModuleView)
}

/**
 * An opt-in annotation denoting that a function or type mutates global state of an [RModule].
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn("This function or type mutates global active state of a module and should" +
        "only be used by RModule implementations, with caution")
annotation class ModuleActiveState