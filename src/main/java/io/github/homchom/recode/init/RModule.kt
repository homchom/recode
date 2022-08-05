package io.github.homchom.recode.init

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.Listener

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

/**
 * Listens to [event], running [listener] if the module is enabled.
 */
inline fun <C, R : Any> RModule.listenTo(
    event: CustomEvent<C, R>,
    crossinline listener: Listener<C, R>
) {
    event.listen { context, result -> if (isEnabled) listener(context, result) else result }
}