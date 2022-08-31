package io.github.homchom.recode.feature

import io.github.homchom.recode.init.*
import io.github.homchom.recode.util.unmodifiable

// TODO: finish and document these

sealed interface Configurable : RModule {
    val name: String
}

/**
 * Builds a [Feature].
 */
fun feature(name: String, builder: ModuleBuilderScope): Feature =
    FeatureBuilder(name, builder)

/**
 * Builds a [FeatureGroup].
 */
fun featureGroup(
    name: String,
    vararg features: Feature,
    builder: ModuleBuilderScope? = null
): FeatureGroup {
    return FeatureGroupBuilder(name, features.toList().unmodifiable(), builder)
}

interface Feature : Configurable, ActiveStateModule

private class FeatureBuilder(
    override val name: String,
    moduleBuilder: ModuleBuilderScope
) : Feature, ActiveStateModule by strongModule(builder = moduleBuilder)

sealed interface FeatureGroup : Configurable {
    val features: List<Feature>
}

@OptIn(MutatesModuleState::class)
private class FeatureGroupBuilder(
    override val name: String,
    override val features: List<Feature>,
    moduleBuilder: ModuleBuilderScope? = null
) : FeatureGroup, RModule by module(builder = {
    onLoad {
        for (feature in features) addParent(feature)
    }

    onEnable {
        for (feature in features) feature.enable()
    }

    onDisable {
        for (feature in features) feature.disable()
    }

    moduleBuilder?.invoke(this)
})