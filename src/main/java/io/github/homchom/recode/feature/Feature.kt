package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [Feature] with [builder].
 *
 * This is provided as a convenience function; for more complex Features, use the more generic
 * [featureDetail] function.
 */
fun <R : RModule> feature(name: String, builder: ModuleDetail<Feature, R>) = module(featureDetail(name), builder)

class Feature(
    override val name: String,
    moduleDelegate: ExposedModule
) : Configurable, ExposedModule by moduleDelegate

class FeatureGroup(
    override val name: String,
    val features: List<Feature>,
    moduleDelegate: RModule
) : Configurable, RModule by moduleDelegate

fun featureDetail(name: String) = ModuleFlavor { Feature(name, it) }

fun featureGroup(name: String, vararg features: Feature) =
    ModuleFlavor { module ->
        module.onLoad {
            for (feature in features) feature.depend(module)
        }

        module.onEnable {
            for (feature in features) feature.assert()
        }

        module.onDisable {
            for (feature in features) feature.unassert()
        }

        FeatureGroup(name, features.toList(), module)
    }