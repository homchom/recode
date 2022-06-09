package io.github.homchom.recode.init

import io.github.homchom.recode.event.REvent
import io.github.homchom.recode.event.RHook

sealed interface RModule {
    val definition: ModuleDefinition
    val dependencies: List<RModule>

    val isEnabled: Boolean

    fun enable()
    fun disable()

    fun tryEnable() {
        if (!isEnabled) enable()
    }

    fun tryDisable() {
        if (isEnabled) disable()
    }

    fun addDependency(dependency: ModuleDefinition)

    operator fun ModuleDefinition.invoke(): RModule
}

sealed interface ModuleDependency : RModule {
    val references: List<RModule>

    fun addReference(reference: RModule)

    fun checkReferences() {
        if (!definition.isPersistent) {
            if (references.any { it.isEnabled }) tryEnable() else tryDisable()
        }
    }
}

interface ModuleDefinition {
    val isPersistent get() = false

    val dependencies: List<ModuleDefinition>

    fun RModule.onLoad()
    fun RModule.onEnable()
    fun RModule.onDisable()

    fun none() = emptyList<ModuleDefinition>()
}

inline fun <T, R> RModule.addToEvent(event: REvent<T, R>, crossinline listener: R.(T) -> Unit) =
    event.listen { context -> if (isEnabled) listener(context) }

inline fun <T> RModule.addToHook(hook: RHook<T>, crossinline listener: (T) -> Unit) =
    hook.listen { context -> if (isEnabled) listener(context) }