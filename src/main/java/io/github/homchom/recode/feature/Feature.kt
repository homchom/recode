package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.unmodifiable

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [Feature].
 */
inline fun feature(name: String, builder: ModuleBuilderScope): Feature =
    ExposedFeature(name, strongExposedModule(builder))

/**
 * Builds a [FeatureGroup].
 */
@OptIn(MutatesModuleState::class)
inline fun featureGroup(
    name: String,
    vararg features: Feature,
    builder: ModuleBuilderScope = {}
): FeatureGroup {
    return ExposedFeatureGroup(name, features.toList().unmodifiable(), module {
        onLoad {
            for (feature in features) addParent(feature)
        }

        onEnable {
            for (feature in features) feature.enable()
        }

        onDisable {
            for (feature in features) feature.disable()
        }

        builder()
    })
}

interface Feature : Configurable, ExposedModule

sealed interface FeatureGroup : Configurable {
    val features: List<Feature>
}

/**
 * A [Feature] that delegates to an [ExposedModule].
 */
class ExposedFeature(override val name: String, module: ExposedModule) : Feature, ExposedModule by module

/**
 * A [FeatureGroup] that delegates to an [ExposedModule].
 */
class ExposedFeatureGroup(
    override val name: String,
    override val features: List<Feature>,
    module: RModule
) : FeatureGroup, RModule by module