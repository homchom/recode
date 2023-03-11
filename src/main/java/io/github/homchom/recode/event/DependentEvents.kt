package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.*
import kotlinx.coroutines.flow.Flow

inline fun <T> DependentListenable(delegate: Listenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentListenable(delegate, module(builder = dependencyBuilder))

inline fun <T> DependentStateListenable(delegate: StateListenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentStateListenable(delegate, module(builder = dependencyBuilder))

inline fun <T> DependentEvent(
    delegate: SharedEvent<T> = createEvent(),
    dependencyBuilder: ModuleBuilderScope
): DependentEvent<T> {
    return DependentEvent(delegate, module(builder = dependencyBuilder))
}

inline fun <T> DependentStateEvent(delegate: StateEvent<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentStateEvent(delegate, module(builder = dependencyBuilder))

inline fun <T> DependentStateEvent(initialValue: T, dependencyBuilder: ModuleBuilderScope) =
    DependentStateEvent(createStateEvent(initialValue), dependencyBuilder)

inline fun <T, R : Any> DependentHook(
    delegate: CustomHook<T, R> = createHook(),
    dependencyBuilder: ModuleBuilderScope
): DependentHook<T, R> {
    return DependentHook(delegate, module(builder = dependencyBuilder))
}

/**
 * A [Listenable] with a [dependency] that is respected by listening modules.
 */
class DependentListenable<T>(
    private val delegate: Listenable<T>,
    private val dependency: RModule
) : Listenable<T> {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * @see DependentListenable
 * @see StateListenable
 */
class DependentStateListenable<T>(
    private val delegate: StateListenable<T>,
    private val dependency: RModule
) : StateListenable<T> by delegate {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * @see DependentListenable
 * @see SharedEvent
 */
class DependentEvent<T>(
    private val delegate: SharedEvent<T>,
    private val dependency: RModule
) : SharedEvent<T> by delegate {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * @see DependentListenable
 * @see StateEvent
 */
class DependentStateEvent<T>(
    private val delegate: StateEvent<T>,
    private val dependency: RModule
) : StateEvent<T> by delegate {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * @see DependentListenable
 * @see CustomHook
 */
class DependentHook<T, R : Any>(
    private val delegate: CustomHook<T, R>,
    private val dependency: RModule
) : CustomHook<T, R> by delegate {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)

    override fun hookFrom(module: HookableModule, listener: HookListener<T, R>) {
        dependency.addParent(module)
        delegate.hookFrom(module, listener)
    }
}

private fun <T> Listenable<T>.getNotificationsDependent(module: ExposedModule, dependency: RModule): Flow<T> {
    module.depend(dependency)
    return getNotificationsFrom(module)
}