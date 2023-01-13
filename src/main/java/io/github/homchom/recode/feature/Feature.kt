package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.collections.immutable

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [Feature].
 */
inline fun feature(name: String, builder: ModuleBuilderScope): Feature =
    ExposedFeature(name, strongExposedModule(ModuleBuilder(builder)))

/**
 * Builds a [FeatureGroup].
 */
fun featureGroup(name: String, vararg features: Feature): FeatureGroup =
    FeatureGroupDetail(*features).let { ExposedFeatureGroup(name, it, module(it)) }

interface Feature : Configurable, ExposedModule

sealed interface FeatureGroup : Configurable {
    val features: List<Feature>
}

@OptIn(MutatesModuleState::class)
class FeatureGroupDetail(vararg features: Feature) : ModuleDetail {
    val features = features.immutable()

    override fun children() = emptyModuleList()

    override fun ExposedModule.onLoad() {
        for (feature in features) addParent(feature)
    }

    override fun ExposedModule.onEnable() {
        for (feature in features) feature.enable()
    }

    override fun ExposedModule.onDisable() {
        for (feature in features) feature.disable()
    }
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
    detail: FeatureGroupDetail,
    module: RModule
) : FeatureGroup, RModule by module {
    override val features by detail::features
}