package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a simple [Feature] with [builder].
 *
 * This is provided as a convenience function; for more complex feature modules, use the more generic
 * [io.github.homchom.recode.lifecycle.module] function.
 */
fun feature(name: String, builder: ModuleBuilder) = module(featureDetail(name), builder)

class Feature(
    override val name: String,
    moduleDelegate: ExposedModule
) : Configurable, ExposedModule by moduleDelegate

class FeatureGroup(
    override val name: String,
    val features: List<Feature>,
    moduleDelegate: RModule
) : Configurable, RModule by moduleDelegate

fun featureDetail(name: String) = ModuleDetail<ExposedModule, Feature> { Feature(name, it) }

fun featureGroupDetail(name: String, vararg features: Feature) =
    ModuleDetail<ExposedModule, FeatureGroup> { module ->
        module.onLoad {
            for (feature in features) feature.depend(module)
        }

        module.onEnable {
            for (feature in features) feature.enable()
        }

        module.onDisable {
            for (feature in features) feature.disable()
        }

        FeatureGroup(name, features.toList(), module)
    }