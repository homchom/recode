package io.github.homchom.recode.event

import io.github.homchom.recode.lifecycle.ModuleBuilderScope
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.module
import kotlinx.coroutines.flow.Flow

inline fun <T> DependentListenable(delegate: Listenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentListenable(delegate, module(builder = dependencyBuilder))

inline fun <T> DependentStateListenable(delegate: StateListenable<T>, dependencyBuilder: ModuleBuilderScope) =
    DependentStateListenable(delegate, module(builder = dependencyBuilder))

inline fun <T, R : Any> DependentEvent(delegate: CustomEvent<T, R>, dependencyBuilder: ModuleBuilderScope) =
    DependentEvent(delegate, module(builder = dependencyBuilder))

inline fun <T, R : Any, I> DependentBufferedEvent(
    delegate: BufferedCustomEvent<T, R, I>,
    dependencyBuilder: ModuleBuilderScope
): DependentBufferedEvent<T, R, I> {
    return DependentBufferedEvent(delegate, module(builder = dependencyBuilder))
}

/**
 * A [Listenable] with a [dependency] that is respected by listening modules.
 */
class DependentListenable<T>(
    private val delegate: Listenable<T>,
    private val dependency: RModule
) : Listenable<T> {
    override fun getNotificationsFrom(module: RModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * @see DependentListenable
 * @see StateListenable
 */
class DependentStateListenable<T>(
    private val delegate: StateListenable<T>,
    private val dependency: RModule
) : StateListenable<T> {
    override val currentState get() = delegate.currentState

    override fun getNotificationsFrom(module: RModule) =
        delegate.getNotificationsDependent(module, dependency)
}

/**
 * @see DependentListenable
 * @see CustomEvent
 */
class DependentEvent<T, R : Any>(
    private val delegate: CustomEvent<T, R>,
    private val dependency: RModule
) : CustomEvent<T, R> {
    override fun getNotificationsFrom(module: RModule) =
        delegate.getNotificationsDependent(module, dependency)

    override suspend fun run(context: T) = delegate.run(context)
}

/**
 * @see DependentListenable
 * @see BufferedCustomEvent
 */
class DependentBufferedEvent<T, R : Any, I>(
    private val delegate: BufferedCustomEvent<T, R, I>,
    private val dependency: RModule
) : BufferedCustomEvent<T, R, I> {
    override fun getNotificationsFrom(module: RModule) =
        delegate.getNotificationsDependent(module, dependency)

    override suspend fun run(input: I) = delegate.run(input)

    override fun stabilize() = delegate.stabilize()
}

private fun <T> Listenable<T>.getNotificationsDependent(module: RModule, dependency: RModule): Flow<T> {
    module.depend(dependency)
    return getNotificationsFrom(module)
}