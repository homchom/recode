package io.github.homchom.recode.feature

import io.github.homchom.recode.init.*

// TODO: finish and document these

sealed interface Configurable : ActiveStateModule {
    val name: String
}

/**
 * Builds a [Feature].
 */
fun feature(name: String, builder: StrongModuleBuilderScope): Feature =
    FeatureBuilder(name, builder)

interface Feature : Configurable

private class FeatureBuilder(
    override val name: String,
    moduleBuilder: StrongModuleBuilderScope
) : Feature, ActiveStateModule by strongModule(builder = moduleBuilder)

sealed class FeatureGroup(
    override val name: String,
    private val module: ActiveStateModule = basicStrongModule()
) : Configurable, ActiveStateModule by module {
    abstract val features: List<Feature>

    @MutatesModuleState
    override fun load() {
        module.load()
        for (feature in features) addAsDependency(feature)
    }

    @MutatesModuleState
    override fun enable() {
        module.enable()
        for (feature in features) feature.enable()
    }

    @MutatesModuleState
    override fun disable() {
        module.disable()
        for (feature in features) feature.disable()
    }
}