package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.MOD_ID
import io.github.homchom.recode.Recode
import io.github.homchom.recode.feature.AddsFeature
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.id
import io.github.homchom.recode.render.RGB
import io.github.homchom.recode.ui.text.toVanilla
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

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

        val packDescription = Component.text("[$MOD_ID] ")
            .append(Component.translatable("resourcePack.recode.$id")
                .color(TextColor.color(displayColor.hex))
            )

        ResourceManagerHelper.registerBuiltinResourcePack(
            id(id),
            Recode,
            packDescription.toVanilla(),
            activationType
        )
    }
}