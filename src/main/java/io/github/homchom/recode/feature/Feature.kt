package io.github.homchom.recode.feature

import io.github.homchom.recode.init.RModule

abstract class Feature(name: String) : Configurable(name) {
    override val isPersistent = true
}

// TODO: update when config is integrated
abstract class FeatureGroup(name: String) : Configurable(name) {
    abstract val features: List<Feature>

    override fun RModule.onLoad() = forEachFeature { it.addDependency(definition) }

    override fun RModule.onEnable() = forEachFeature { it.enable() }

    override fun RModule.onDisable() = forEachFeature { it.disable() }

    private inline fun RModule.forEachFeature(action: (RModule) -> Unit) {
        for (feature in features) action(feature())
    }
}