@file:JvmName("MessageStacking")

package io.github.homchom.recode.feature.social

import io.github.homchom.recode.MOD_NAME
import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.ui.text.literalText
import io.github.homchom.recode.ui.text.style
import io.github.homchom.recode.ui.text.toVanilla
import io.github.homchom.recode.ui.text.translatedText
import net.minecraft.client.GuiMessageTag

object MessageStacks {
    private const val STACK_TAG_PREFIX = "$MOD_NAME stacked x"

    fun tag(amount: Int) = GuiMessageTag(
        ColorPalette.AQUA.hex,
        GuiMessageTag.Icon.CHAT_MODIFIED,
        translatedText(
            "chat.tag.recode.stacked",
            style().aqua(),
            arrayOf(literalText(amount))
        ).toVanilla(),
        "$STACK_TAG_PREFIX$amount"
    )
}