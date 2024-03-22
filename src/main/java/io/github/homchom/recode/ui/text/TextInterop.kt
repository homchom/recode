@file:JvmName("TextInterop")

package io.github.homchom.recode.ui.text

import com.google.common.cache.CacheBuilder
import io.github.homchom.recode.ui.text.LegacyCodeRemover.plainText
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.key.Key
import net.kyori.adventure.platform.fabric.FabricClientAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.network.chat.FormattedText.StyledContentConsumer
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.StringDecomposer
import java.util.*
import java.time.Duration as JDuration

typealias VanillaComponent = net.minecraft.network.chat.Component
typealias VanillaStyle = net.minecraft.network.chat.Style

/**
 * Converts this [Component] to a [VanillaComponent].
 */
fun Component.toVanilla() = FabricClientAudiences.of().toNative(this)

/**
 * Converts this [ComponentLike] to a [VanillaComponent].
 */
fun ComponentLike.toVanillaComponent() = asComponent().toVanilla()

/**
 * Converts this [Key] to a [ResourceLocation].
 */
fun Key.toResourceLocation() = ResourceLocation(namespace(), value())

/**
 * Converts this [VanillaStyle] to a [Style].
 */
fun VanillaStyle.toAdventure() = VanillaComponent.empty()
    .withStyle(this)
    .asComponent()
    .style()

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

/**
 * An object with a cache that removes § formatting codes from [Component] literals.
 *
 * @see remove
 * @see plainText
 */
object LegacyCodeRemover {
    private val legacyCodeRegex = regex {
        str("§")
        group {
            any("0-9a-fk-o")
            or
            str("x")
            group {
                str("§")
                any("0-9a-f")
            } * 6
        }
    }

    private val stringCache = CacheBuilder.newBuilder()
        .expireAfterAccess(JDuration.ofSeconds(1))
        .build<String, String>()

    private val textCache = CacheBuilder.newBuilder()
        .expireAfterAccess(JDuration.ofSeconds(1))
        .build<Component, String>()

    /**
     * Removes all § formatting codes from [componentString].
     */
    fun removeCodes(componentString: String): String = stringCache.get(componentString) {
        componentString.replace(legacyCodeRegex, "")
    }

    fun plainText(text: Component): String = textCache.get(text) {
        removeCodes(PlainTextComponentSerializer.plainText().serialize(text))
    }
}