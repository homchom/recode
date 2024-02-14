package io.github.homchom.recode.feature.visual

import io.github.homchom.recode.MOD_ID
import io.github.homchom.recode.Recode
import io.github.homchom.recode.feature.registerFeature
import io.github.homchom.recode.id
import io.github.homchom.recode.render.IntegralColor
import io.github.homchom.recode.render.toColor
import io.github.homchom.recode.ui.text.VanillaComponent
import io.github.homchom.recode.ui.text.VanillaStyle
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType

object FBuiltInResourcePacks {
    init {
        registerFeature("Built-in Resource Packs") {
            registerPack("better_unicode", 0x6770ff.toColor())
        }
    }

    private fun registerPack(
        id: String,
        displayColor: IntegralColor,
        activationType: ResourcePackActivationType = ResourcePackActivationType.DEFAULT_ENABLED
    ) {
        // https://github.com/KyoriPowered/adventure-platform-fabric/issues/122
        val packDescription = VanillaComponent.literal("[$MOD_ID] ")
            .append(VanillaComponent.translatable("resourcePack.recode.$id")
                .withStyle(VanillaStyle.EMPTY.withColor(displayColor.toInt()))
            )

        ResourceManagerHelper.registerBuiltinResourcePack(
            id(id),
            Recode,
            packDescription,
            activationType
        )
    }
}