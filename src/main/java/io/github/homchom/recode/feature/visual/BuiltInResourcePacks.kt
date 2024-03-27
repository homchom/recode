package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.MOD_ID
import io.github.homchom.recode.Recode
import io.github.homchom.recode.feature.AddsFeature
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.id
import io.github.homchom.recode.render.RGB
import io.github.homchom.recode.ui.text.style
import io.github.homchom.recode.ui.text.text
import io.github.homchom.recode.ui.text.toVanillaComponent
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType

@OptIn(AddsFeature::class)
object FBuiltInResourcePacks {
    init {
        registerFeature("Built-in Resource Packs") {
            registerPack("better_unicode", RGB(0x6770ff))
        }
    }

    private fun registerPack(
        id: String,
        displayColor: RGB,
        activationType: ResourcePackActivationType = ResourcePackActivationType.DEFAULT_ENABLED
    ) {

        val packDescription = text {
            literal("[$MOD_ID] ")
            translate("resourcePack.recode.$id", style().color(displayColor))
        }

        ResourceManagerHelper.registerBuiltinResourcePack(
            id(id),
            Recode,
            packDescription.toVanillaComponent(),
            activationType
        )
    }
}