package io.github.homchom.recode.feature

import io.github.homchom.recode.init.*

// TODO: update feature and featureGroup when config is integrated
inline fun feature(name: String, builder: StrongModuleBuilderScope) =
    module({
        Feature(BasicStrongModule(dependencies, onLoad.action, onEnable.action, onDisable.action))
    }, builder)

@JvmInline
value class Feature(val module: StrongModule) : StrongModule by module

inline fun featureGroup(
    name: String,
    features: Array<out Feature>,
    builder: StrongModuleBuilderScope = {}
): RModule {
    return strongModule {
        onLoad {
            for (feature in features) feature.addDependency(this)
        }

        onEnable {
            for (feature in features) feature.enable()
        }

        onDisable {
            for (feature in features) feature.disable()
        }

        builder()
    }
}