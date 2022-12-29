package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi

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
    fun addChild(module: ExposedModule)
}

/**
 * A [RModule] that can hook onto [Hook] invocations.
 */
interface HookableModule : RModule {
    /**
     * @see Hook.listenFrom
     */
    fun <C, R> Hook<C, R>.hook(listener: HookListener<C, R>) =
        listenFrom(this@HookableModule, listener)

    /**
     * @see unitListenFrom
     */
    fun <C, R> Hook<C, R>.listen(hook: (C) -> Unit) = unitListenFrom(this@HookableModule, hook)
}

/**
 * An [HookableModule] with a [CoroutineScope] and exposed functions for mutating active state. This
 * should be used when creating module subtypes, not when creating top-level modules directly.
 */
interface ExposedModule : HookableModule {
    val coroutineScope: CoroutineScope

    //fun NotificationFlow.listen() = listenFrom(this@ExposedModule)

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
    fun addUsage(module: ExposedModule)

    /**
     * Tells this module that [module] is not currently using it.
     */
    @MutatesModuleState
    fun removeUsage(module: ExposedModule)
}

/**
 * A [HookableModule] that is always enabled. Useful for listening to events globally. Don't use
 * inside another module, and prefer listening to more localized modules when applicable.
 */
@OptIn(DelicateCoroutinesApi::class)
val GlobalModule: HookableModule get() = GlobalExposedModule

/**
 * An [ExposedModule] that is always enabled. Don't use inside another module, and prefer using
 * modules with properly confined coroutine scopes.
 *
 * @see kotlinx.coroutines.CoroutineScope
 */
@OptIn(MutatesModuleState::class)
@DelicateCoroutinesApi
object GlobalExposedModule : ExposedModule by buildStrongExposedModule() {
    init {
        enable()
    }
}

/**
 * An opt-in annotation denoting that something mutates global active state of an [ExposedModule].
 */
@RequiresOptIn("This mutates global active state of a module and should only be " +
        "used by RModule implementations, with caution")
annotation class MutatesModuleState