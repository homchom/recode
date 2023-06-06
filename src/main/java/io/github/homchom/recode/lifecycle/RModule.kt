package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.GroupListenable
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.util.KeyHashable
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
interface RModule : KeyHashable {
    val children: Set<RModule>

    val isLoaded: Boolean
    val isEnabled: Boolean

    fun addParent(module: RModule)
    fun addChild(module: ExposedModule)

    fun depend(module: RModule) {
        if (module !in children) module.addParent(this)
    }

    fun <T : Any, R : Any> Detector<T, R>.detect(input: T?) = detectFrom(this@RModule, input)

    suspend fun <T : Any, R : Any> Requester<T, R>.request(input: T) =
        requestFrom(this@RModule, input)

    fun <T, S : Listenable<out T>> GroupListenable<T>.add(event: S) =
        addFrom(this@RModule, event)

    suspend fun <R : Any> Requester<Unit, R>.request() = request(Unit)
}

/**
 * An [RModule] with a [CoroutineScope].
 */
interface CoroutineModule : RModule, CoroutineScope {
    val <T> Listenable<T>.notifications get() = getNotificationsFrom(this@CoroutineModule)

    fun <T> Listenable<T>.listen(block: Flow<T>.() -> Flow<T>) =
        listenFrom(this@CoroutineModule, block)

    fun <T> Listenable<T>.listenEach(block: (T) -> Unit) =
        listenEachFrom(this@CoroutineModule, block)
}

/**
 * A [CoroutineModule] that can listen for [Listenable] notifications, with exposed functions
 * for mutating active state. This should be used when creating module subtypes, not when creating
 * top-level modules directly.
 */
interface ExposedModule : CoroutineModule {
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
@DelicateCoroutinesApi
object GlobalModule : ExposedModule by strongExposedModule()

/**
 * An opt-in annotation denoting that something mutates global active state of an [ExposedModule].
 */
@RequiresOptIn("This mutates global active state of a module and should only be " +
        "used by RModule implementations, with caution")
annotation class MutatesModuleState