package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [FeatureModule] with [builder].
 *
 * This is provided as a convenience function; for more complex FeatureModules, use the more generic
 * [io.github.homchom.recode.lifecycle.module] function.
 */
fun featureModule(name: String, builder: ModuleBuilder) = module(feature(name), builder)

class FeatureModule(
    override val name: String,
    moduleDelegate: ExposedModule
) : Configurable, ExposedModule by moduleDelegate

class FeatureGroupModule(
    override val name: String,
    val features: List<FeatureModule>,
    moduleDelegate: RModule
) : Configurable, RModule by moduleDelegate

fun feature(name: String) = ModuleDetail<ExposedModule, FeatureModule> { FeatureModule(name, it) }

fun featureGroup(name: String, vararg features: FeatureModule) =
    ModuleDetail<ExposedModule, FeatureGroupModule> { module ->
        module.onLoad {
            for (feature in features) feature.depend(module)
        }

        module.onEnable {
            for (feature in features) feature.enable()
        }

        module.onDisable {
            for (feature in features) feature.disable()
        }

        FeatureGroupModule(name, features.toList(), module)
    }