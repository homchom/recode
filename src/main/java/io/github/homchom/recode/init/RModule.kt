package io.github.homchom.recode.init

import io.github.homchom.recode.event.Listener
import io.github.homchom.recode.event.REvent
import io.github.homchom.recode.event.hookFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope

/**
 * A group of code with dependencies ("children") and that can be loaded, enabled, and disabled.
 *
 * All modules are either **weak**, meaning they load and unload as needed based on its parents,
 * or **strong**, meaning they remain active even if no parents are enabled.
 *
 * @see module
 * @see strongModule
 */
interface RModule {
    val children: List<RModule>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun addParent(module: RModule)
    fun addChild(module: ActiveStateModule)
}

interface ListenableModule : RModule {
    /**
     * @see REvent.listenFrom
     */
    fun <C, R> REvent<C, R>.listen(listener: Listener<C, R>) =
        listenFrom(this@ListenableModule, listener)

    /**
     * @see hookFrom
     */
    fun <C, R> REvent<C, R>.hook(hook: (C) -> Unit) = hookFrom(this@ListenableModule, hook)
}

interface CoroutineModule : RModule {
    val coroutineScope: CoroutineScope
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
 * A [ListenableModule] that is always enabled. Useful for listening to events globally. Don't use
 * inside another module, and prefer listening to more localized modules when applicable.
 */
val GlobalModule: ListenableModule get() = GlobalBuiltModule

/**
 * A [CoroutineModule] that is always enabled. Don't use inside another module, and prefer using
 * modules with properly confined coroutine scopes.
 *
 * @see kotlinx.coroutines.CoroutineScope
 */
@DelicateCoroutinesApi
val GlobalCoroutineModule: CoroutineModule get() = GlobalBuiltModule

private object GlobalBuiltModule : BuiltModule by strongModule(builder = {}) {
    @DelicateCoroutinesApi
    override val coroutineScope get() = GlobalScope
}

/**
 * An opt-in annotation denoting that something mutates global active state of an [RModule].
 */
@RequiresOptIn("This mutates global active state of a module and should only be " +
        "used by RModule implementations, with caution")
annotation class MutatesModuleState