@file:JvmName("MessageStacking")

package io.github.homchom.recode.feature.social

import io.github.homchom.recode.MOD_NAME
import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.ui.text.literalText
import io.github.homchom.recode.ui.text.style
import io.github.homchom.recode.ui.text.toVanilla
import io.github.homchom.recode.ui.text.translatedText
import io.github.homchom.recode.util.regex.regex
import net.minecraft.client.GuiMessageTag

private val stackTagPrefix get() = "$MOD_NAME stacked x"

private val stackRegex = regex {
    str(stackTagPrefix)
    val amount by digit.oneOrMore()
}

fun stackedMessageTag(amount: Int) = GuiMessageTag(
    ColorPalette.AQUA.hex,
    GuiMessageTag.Icon.CHAT_MODIFIED,
    translatedText(
        "chat.tag.recode.stacked",
        style().aqua(),
        arrayOf(literalText(amount))
    ).toVanilla(),
    "$stackTagPrefix$amount"
)

val GuiMessageTag.stackAmount: Int
    get() {
        val description = logTag ?: return 1
        val match = stackRegex.find(description) ?: return 1
        return match.groupValues[1].toInt()
    }