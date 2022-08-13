package io.github.homchom.recode.feature

import io.github.homchom.recode.init.*

sealed interface Configurable : RModule {
    val name: String
}

fun feature(name: String, builder: StrongModuleBuilderScope): Feature =
    FeatureBuilder(name, builder)

interface Feature : Configurable

private class FeatureBuilder(
    override val name: String,
    moduleBuilder: StrongModuleBuilderScope
) : Feature, RModule by strongModule(moduleBuilder)

sealed class FeatureGroup private constructor(
    override val name: String,
    private val module: RModule
) : Configurable, RModule by module {
    abstract val features: List<RModule>

    // this is used; warning is IntelliJ bug KTIJ-22439
    @Suppress("unused")
    constructor(name: String) : this(name, basicStrongModule())

    @ModuleActiveState
    override fun load() {
        module.load()
        for (feature in features) addAsDependency(feature)
    }

    @ModuleActiveState
    override fun enable() {
        module.enable()
        for (feature in features) feature.enable()
    }

    @ModuleActiveState
    override fun disable() {
        module.disable()
        for (feature in features) feature.disable()
    }
}