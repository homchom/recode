package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.listenFrom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

typealias ModuleAction = ExposedModule.() -> Unit

/**
 * A group of code with dependencies ("children") and that can be loaded, enabled, and disabled.
 *
 * Modules can also [depend] on and [extend] other modules; in general, when all of a module's "parents" are
 * disabled, it disables as well. However, RModule implementations should be *assertive*, meaning that an explicit
 * call to [ExposedModule.assert] will prevent automatic disabling until [ExposedModule.unassert] is also called
 * explicitly.
 *
 * @see module
 */
interface RModule {
    val isEnabled: StateFlow<Boolean>

    /**
     * Adds [children] as children of the module.
     */
    fun depend(vararg children: RModule) {
        for (child in children) child.extend(this)
    }

    /**
     * Adds [parents] as parents of the module.
     */
    fun extend(vararg parents: RModule)

    /**
     * @see Detector.detectFrom
     */
    fun <T : Any, R : Any> Detector<T, R>.detect(input: T?, hidden: Boolean = false) =
        detectFrom(this@RModule, input, hidden)

    /**
     * @throws kotlinx.coroutines.TimeoutCancellationException
     *
     * @see Requester.requestFrom
     */
    suspend fun <T : Any, R : Any> Requester<T, R>.request(input: T, hidden: Boolean = false) =
        requestFrom(this@RModule, input, hidden)

    /**
     * @throws kotlinx.coroutines.TimeoutCancellationException
     *
     * @see Requester.requestFrom
     */
    suspend fun <R : Any> Requester<Unit, R>.request(): R = request(Unit)
}

/**
 * A [RModule] and [CoroutineScope] with exposed functions for mutating and decorating the module.
 * This should be not be used to create top-level modules directly.
 *
 * Implementations of ExposedModule should ensure that their CoroutineScopes are cancelled at initialization,
 * to defend against erroneous coroutine launching before the first invocation of [assert].
 *
 * @see ModuleDetail
 */
interface ExposedModule : RModule, CoroutineScope {
    /**
     * Loads the module for the first time. If the module has already been loaded, this function has no effect.
     */
    fun load()

    /**
     * Enables the module if it is disabled. If the module has not been loaded, [load] is also called.
     */
    fun assert()

    /**
     * Disables the module if it is enabled and none of its parents are enabled.
     */
    fun unassert()

    /**
     * Registers [action] to be invoked when the module loads.
     */
    fun onLoad(action: ModuleAction)

    /**
     * Registers [action] to be invoked when the module enables.
     */
    fun onEnable(action: ModuleAction)

    /**
     * Registers [action] to be invoked when the module disables.
     */
    fun onDisable(action: ModuleAction)

    /**
     * @see Listenable.listenFrom
     */
    fun <T> Listenable<T>.listen(block: Flow<T>.() -> Flow<T>) =
        listenFrom(this@ExposedModule, block)

    /**
     * @see Listenable.listenEachFrom
     */
    fun <T> Listenable<T>.listenEach(block: (T) -> Unit) =
        listenEachFrom(this@ExposedModule, block)
}