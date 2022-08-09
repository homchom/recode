package io.github.homchom.recode.init

import io.github.homchom.recode.event.Listener
import io.github.homchom.recode.event.REvent

/**
 * A view of a module, used by [RModule] to restrict its access to read-only.
 */
interface ModuleView {
    val dependencies: List<ModuleView>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun <C, R> listenTo(event: REvent<C, R, *>, listener: Listener<C, R>) =
        event.listen { context, result ->
            if (isEnabled) listener(context, result) else result
        }
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
    @ModuleMutableState
    fun load()

    @ModuleMutableState
    fun tryLoad() {
        if (!isLoaded) load()
    }

    @ModuleMutableState
    fun enable()

    @ModuleMutableState
    fun disable()

    @ModuleMutableState
    fun addDependency(module: RModule)

    @ModuleMutableState
    fun addUsage(module: RModule)

    @ModuleMutableState
    fun removeUsage(module: RModule)
}

/**
 * A global handle of an [RModule], used for adding it as dependencies to other modules.
 */
interface ModuleHandle {
    @ModuleMutableState
    fun addAsDependency(to: RModule)
}

/**
 * An opt-in annotation denoting that a function or type mutates global state of an [RModule].
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@RequiresOptIn("This function or type mutates global state of a module and should only" +
        "be used by RModule implementations, with caution")
annotation class ModuleMutableState