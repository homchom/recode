@file:JvmName("TextInterop")

package io.github.homchom.recode.ui.text

import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.platform.fabric.FabricClientAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.minecraft.network.chat.FormattedText.StyledContentConsumer
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.StringDecomposer
import java.util.*

typealias VanillaComponent = net.minecraft.network.chat.Component
typealias VanillaStyle = net.minecraft.network.chat.Style

/**
 * Converts this [Component] to a [VanillaComponent].
 */
fun Component.toVanilla() = FabricClientAudiences.of().toNative(this)

/**
 * Converts this [VanillaStyle] to a [Style].
 */
fun VanillaStyle.toAdventure() = VanillaComponent.empty()
    .withStyle(this)
    .asComponent()
    .style()

/**
 * Converts this [Style] to a [VanillaStyle].
 */
fun Style.toVanilla(): VanillaStyle = Component.text()
    .style(this)
    .build()
    .toVanilla()
    .style

/**
 * Converts this [Component] to a [FormattedCharSequence].
 *
 * @param inLanguageOrder Whether to order the text according to the client's [net.minecraft.locale.Language].
 */
fun Component.toFormattedCharSequence(inLanguageOrder: Boolean = true): FormattedCharSequence {
    if (inLanguageOrder) return toVanilla().visualOrderText

    return FormattedCharSequence { sink ->
        val consumer = StyledContentConsumer { style, string ->
            if (StringDecomposer.iterateFormatted(string, style, sink)) {
                Optional.empty()
            } else {
                Optional.of(Unit)
            }
        }
        // isEmpty, not isPresent; the Minecraft source is bugged
        toVanilla().visit(consumer, VanillaStyle.EMPTY).isEmpty
    }
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