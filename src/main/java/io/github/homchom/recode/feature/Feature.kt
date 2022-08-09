package io.github.homchom.recode.feature

import io.github.homchom.recode.init.*

/**
 * Builds a feature.
 *
 * @see module
 */
// TODO: update feature and featureGroup when config is integrated
inline fun feature(name: String, builder: ModuleBuilderScope) =
    module({
        Feature(strongModule(dependencies, onLoad.action, onEnable.action, onDisable.action))
    }, builder)

@JvmInline
value class Feature(private val module: BaseModule) : BaseModule by module

/**
 * Builds a feature group.
 *
 * @see module
 */
@OptIn(ModuleMutableState::class)
inline fun featureGroup(
    name: String,
    features: Array<out Feature>,
    builder: ModuleBuilderScope = {}
): ModuleHandle {
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