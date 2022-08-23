package io.github.homchom.recode.feature

import io.github.homchom.recode.init.ActiveStateModule
import io.github.homchom.recode.init.MutatesModuleState
import io.github.homchom.recode.init.StrongModuleBuilderScope
import io.github.homchom.recode.init.strongModule
import io.github.homchom.recode.util.unmodifiable

// TODO: finish and document these

sealed interface Configurable : ActiveStateModule {
    val name: String
}

/**
 * Builds a [Feature].
 */
fun feature(name: String, builder: StrongModuleBuilderScope): Feature =
    FeatureBuilder(name, builder)

/**
 * Builds a [FeatureGroup].
 */
fun featureGroup(
    name: String,
    vararg features: Feature,
    builder: StrongModuleBuilderScope? = null
): FeatureGroup {
    return FeatureGroupBuilder(name, features.toList().unmodifiable(), builder)
}

interface Feature : Configurable

private class FeatureBuilder(
    override val name: String,
    moduleBuilder: StrongModuleBuilderScope
) : Feature, ActiveStateModule by strongModule(builder = moduleBuilder)

sealed interface FeatureGroup : Configurable {
    val features: List<Feature>
}

@OptIn(MutatesModuleState::class)
private class FeatureGroupBuilder(
    override val name: String,
    override val features: List<Feature>,
    moduleBuilder: StrongModuleBuilderScope? = null
) : FeatureGroup, ActiveStateModule by strongModule(builder = {
    onLoad {
        for (feature in features) addAsDependency(feature)
    }

    onEnable {
        for (feature in features) feature.enable()
    }

    onDisable {
        for (feature in features) feature.disable()
    }

    moduleBuilder?.invoke(this)
})