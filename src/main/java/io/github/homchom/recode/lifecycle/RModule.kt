package io.github.homchom.recode.lifecycle

import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.Listenable
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.util.KeyHashable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow

typealias ModuleAction = ExposedModule.() -> Unit

/**
 * A group of code with dependencies ("children") and that can be loaded, enabled, and disabled.
 *
 * Modules can also [extend] and [depend] on other modules; when all of a module's "parents" are disabled,
 * it disables as well.
 *
 * @see module
 */
// TODO: revisit extend-depend scheme and safety (and replace children property with parents)
// TODO: figure out replacement for strong modules
interface RModule : KeyHashable {
    val children: Set<RModule>

    val hasBeenLoaded: Boolean
    val isEnabled: Boolean

    @ModuleUnsafe
    fun addChild(child: ExposedModule)

    /**
     * Adds [parents] as parents of the module.
     */
    fun extend(vararg parents: RModule)

    /**
     * Adds [children] as children of the module.
     */
    fun depend(vararg children: RModule) {
        for (child in children) child.extend(this)
    }

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
    suspend fun <R : Any> Requester<Unit, R>.request() = request(Unit)
}

/**
 * An [RModule] with a [CoroutineScope].
 */
interface CoroutineModule : RModule, CoroutineScope {
    val <T> Listenable<T>.notifications get() = getNotificationsFrom(this@CoroutineModule)

    /**
     * @see Listenable.listenFrom
     */
    fun <T> Listenable<T>.listen(block: Flow<T>.() -> Flow<T>) =
        listenFrom(this@CoroutineModule, block)

    /**
     * @see Listenable.listenEachFrom
     */
    fun <T> Listenable<T>.listenEach(block: (T) -> Unit) =
        listenEachFrom(this@CoroutineModule, block)
}

/**
 * A [CoroutineModule] that can listen for [Listenable] notifications, with exposed functions
 * for mutating and decorating the module. This should be not be used to create top-level modules directly.
 *
 * @see ModuleDetail
 */
interface ExposedModule : CoroutineModule {
    /**
     * Loads the module for the first time. If the module has already been loaded, this function has no effect.
     */
    fun load()

    /**
     * Enables the module if it is disabled. If the module has not been loaded, [load] is also called.
     */
    fun enable()

    /**
     * Disables the module if it is enabled.
     */
    fun disable()

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
     * Tells this module that [module] is currently using it.
     */
    @ModuleUnsafe
    fun addUsage(module: ExposedModule)

    /**
     * Tells this module that [module] is not currently using it.
     */
    @ModuleUnsafe
    fun removeUsage(module: ExposedModule)
}

/**
 * An [CoroutineModule] that is always enabled. Don't use inside another module, and prefer using more localized
 * modules with properly confined coroutine scopes.
 *
 * @see kotlinx.coroutines.CoroutineScope
 */
@DelicateCoroutinesApi
data object GlobalModule : CoroutineModule {
    override val children get() = _children.toSet()
    private val _children = mutableSetOf<RModule>()

    override val hasBeenLoaded get() = true
    override val isEnabled get() = true
    override val coroutineContext get() = GlobalScope.coroutineContext

    @ModuleUnsafe
    override fun addChild(child: ExposedModule) {
        if (child !in _children) {
            _children += child
            child.enable()
        }
    }

    override fun extend(vararg parents: RModule) {}
}

/**
 * An opt-in annotation denoting that something mutates global active state of an [ExposedModule].
 */
@RequiresOptIn("This unsafely mutates global active state of a module and should only be " +
        "used by RModule implementations, with caution")
annotation class ModuleUnsafe