@file:JvmName("TextInterop")

package io.github.homchom.recode.ui.text

import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.platform.fabric.FabricClientAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

typealias VanillaComponent = net.minecraft.network.chat.Component
typealias VanillaStyle = net.minecraft.network.chat.Style

fun Component.toNative() = FabricClientAudiences.of().toNative(this)

// using serialization here is future-proof; this is worth the inefficiency
fun VanillaStyle.toAdventure(): Style {
    val vanillaText = VanillaComponent.empty().withStyle(this)
    val json = net.minecraft.network.chat.Component.Serializer.toJsonTree(vanillaText)
    val adventureText = GsonComponentSerializer.gson().deserializeFromTree(json)
    return adventureText.style()
}

private val legacyCodeRegex = regex {
    str("§")
    group(RegexModifier.IgnoreCase) {
        any("0-9a-fk-o")
        or
        str("x")
        group {
            str("§")
            any("0-9a-f")
        } * 6
    }
}

/**
 * Removes all § formatting codes from [componentString].
 */
fun removeLegacyCodes(componentString: String) = componentString.replace(legacyCodeRegex, "")