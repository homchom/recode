package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow

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
 * A [RModule] that can hook onto [Hook] events.
 */
interface HookableModule : RModule {
    /**
     * @see Hook.hookFrom
     */
    fun <T, R> Hook<T, R>.hook(listener: HookListener<T, R>) = hookFrom(this@HookableModule, listener)
}

/**
 * An [HookableModule] with a [CoroutineScope] and that can listen for [Listenable] notifications, with exposed
 * functions for mutating active state. This should be used when creating module 2subtypes, not when creating
 * top-level modules directly.
 */
interface ExposedModule : HookableModule {
    val coroutineScope: CoroutineScope

    fun <T> Listenable<T>.listen(block: Flow<T>.() -> Flow<T>) = listenFrom(this@ExposedModule, block)

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
 * An [ExposedModule] that is always enabled. Don't use inside another module, and prefer using more localized
 * modules with properly confined coroutine scopes.
 *
 * @see kotlinx.coroutines.CoroutineScope
 */
@OptIn(MutatesModuleState::class)
@DelicateCoroutinesApi
object GlobalModule : ExposedModule by strongExposedModule() {
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