package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.ModuleBuilderScope
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.module
import kotlinx.coroutines.flow.Flow

inline fun <T> DependentListenable(delegate: Listenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentListenable(delegate, module(builder = dependencyBuilder))

inline fun <T> DependentStateListenable(delegate: StateListenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentStateListenable(delegate, module(builder = dependencyBuilder))

inline fun <T, R : Any> DependentEvent(
    delegate: CustomEvent<T, R>,
    dependencyBuilder: ModuleBuilderScope
): DependentEvent<T, R> {
    return DependentEvent(delegate, module(builder = dependencyBuilder))
}

inline fun <T> DependentEvent(dependencyBuilder: ModuleBuilderScope) =
    DependentEvent(createEvent<T>(), dependencyBuilder)

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
 * @see CustomEvent
 */
class DependentEvent<T, R : Any>(
    private val delegate: CustomEvent<T, R>,
    private val dependency: RModule
) : CustomEvent<T, R> by delegate {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

private fun <T> Listenable<T>.getNotificationsDependent(module: ExposedModule, dependency: RModule): Flow<T> {
    module.depend(dependency)
    return getNotificationsFrom(module)
}