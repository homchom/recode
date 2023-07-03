package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.MOD_ID
import io.github.homchom.recode.Recode
import io.github.homchom.recode.feature.featureModule
import io.github.homchom.recode.id
import io.github.homchom.recode.render.IntegralColor
import io.github.homchom.recode.render.toColor
import io.github.homchom.recode.ui.text
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType

val FBuiltInResourcePacks = featureModule("Built-in Resource Packs") {
    onLoad {
        registerBuiltInResourcePack("better_unicode", 0x6770ff.toColor())
    }
}

private fun registerBuiltInResourcePack(
    id: String,
    displayColor: IntegralColor,
    activationType: ResourcePackActivationType = ResourcePackActivationType.DEFAULT_ENABLED
) {
    ResourceManagerHelper.registerBuiltinResourcePack(
        id(id),
        Recode,
        text {
            literal("[$MOD_ID] ")
            color(displayColor) { translate("resourcePack.recode.$id") }
        },
        activationType
    )
}