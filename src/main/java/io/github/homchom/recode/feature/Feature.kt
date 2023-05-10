package io.github.homchom.recode.feature

import io.github.homchom.recode.lifecycle.*
import io.github.homchom.recode.util.collections.immutable

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [FeatureModule] with [details] and [builder].
 */
inline fun feature(name: String, vararg details: ModuleDetail, builder: ModuleBuilderScope): FeatureModule =
    SimpleFeatureModule(name, strongExposedModule(*details, builder = builder))

/**
 * Builds a [FeatureModule] with [details].
 */
fun feature(name: String, vararg details: ModuleDetail): FeatureModule =
    SimpleFeatureModule(name, strongExposedModule(*details))

/**
 * Builds a [FeatureGroupModule].
 */
fun featureGroup(name: String, vararg features: FeatureModule): FeatureGroupModule =
    FeatureGroup(*features, name = name).let { SimpleFeatureGroupModule(name, it, module(it)) }

interface FeatureModule : Configurable, ExposedModule

sealed interface FeatureGroupModule : Configurable {
    val features: List<FeatureModule>
}

@OptIn(MutatesModuleState::class)
class FeatureGroup(vararg features: FeatureModule, private val name: String) : ModuleDetail {
    val features = features.immutable()

    override fun children() = emptyModuleList()

    override fun ExposedModule.onLoad() {
        forEachFeature { addParent(it) }
    }

    override fun ExposedModule.onEnable() {
        forEachFeature { it.enable() }
    }

    override fun ExposedModule.onDisable() {
        forEachFeature { it.disable() }
    }

    private inline fun forEachFeature(block: (FeatureModule) -> Unit) {
        for (feature in features) block(feature)
    }
}

/**
 * A simple [FeatureModule] that delegates to an [ExposedModule].
 */
class SimpleFeatureModule(
    override val name: String,
    moduleDelegate: ExposedModule
) : FeatureModule, ExposedModule by moduleDelegate

/**
 * A simple [FeatureGroupModule] that delegates to an [ExposedModule].
 */
class SimpleFeatureGroupModule(
    override val name: String,
    detail: FeatureGroup,
    moduleDelegate: RModule
) : FeatureGroupModule, RModule by moduleDelegate {
    override val features by detail::features
}