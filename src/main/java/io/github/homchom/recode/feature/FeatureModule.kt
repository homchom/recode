package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.collections.immutable

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [FeatureModule].
 */
inline fun feature(name: String, builder: ModuleBuilderScope): FeatureModule =
    SimpleFeatureModule(name, strongExposedModule(ModuleBuilder(builder)))

/**
 * Builds a [FeatureGroupModule].
 */
fun featureGroup(name: String, vararg features: FeatureModule): FeatureGroupModule =
    FeatureGroup(*features).let { SimpleFeatureGroupModule(name, it, module(it)) }

interface FeatureModule : Configurable, ExposedModule

sealed interface FeatureGroupModule : Configurable {
    val features: List<FeatureModule>
}

@OptIn(MutatesModuleState::class)
class FeatureGroup(vararg features: FeatureModule) : ModuleDetail {
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
 * A simple [FeatureModule] that delegates to an [ExposedModule].
 */
class SimpleFeatureModule(override val name: String, module: ExposedModule) : FeatureModule, ExposedModule by module

/**
 * A simple [FeatureGroupModule] that delegates to an [ExposedModule].
 */
class SimpleFeatureGroupModule(
    override val name: String,
    detail: FeatureGroup,
    module: RModule
) : FeatureGroupModule, RModule by module {
    override val features by detail::features
}