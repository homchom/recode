package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.*
import kotlinx.coroutines.flow.Flow

@Suppress("FunctionName")
inline fun <T> DependentListenable(
    delegate: Listenable<T> = createEvent(),
    dependencyBuilder: ModuleBuilderScope
): DependentListenable<T> {
    return DependentListenable(delegate, module(builder = dependencyBuilder))
}

inline fun <T> DependentStateListenable(delegate: StateListenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentStateListenable(delegate, module(builder = dependencyBuilder))

inline fun <T> DependentStateListenable(initialValue: T, dependencyBuilder: ModuleBuilderScope) =
    DependentStateListenable(createStateEvent(initialValue), module(builder = dependencyBuilder))

@Suppress("FunctionName")
inline fun <T, R : Any> DependentHook(
    delegate: CustomHook<T, R> = createHook(),
    dependencyBuilder: ModuleBuilderScope
): DependentHook<T, R> {
    return DependentHook(delegate, module(builder = dependencyBuilder))
}

class DependentListenable<T>(
    private val delegate: Listenable<T>,
    private val dependency: RModule
) : Listenable<T> {
    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

class DependentStateListenable<T>(
    private val delegate: StateListenable<T>,
    private val dependency: RModule
) : StateListenable<T> {
    override val currentState get() = delegate.currentState

    override fun getNotificationsFrom(module: ExposedModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * A [CustomHook] with children. When listened to by a [HookableModule], the children will be implicitly added.
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
    if (module !in dependency.parents) dependency.addParent(module)
    return getNotificationsFrom(module)
}