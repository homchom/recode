package io.github.homchom.recode.ui.text

import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.render.RGB
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration

/**
 * Creates a [StyleWrapper].
 */
fun style(initial: Style = Style.empty()) = StyleWrapper(initial.toBuilder())

/**
 * A wrapper class for idiomatic [Style] creation.
 */
@Suppress("unused")
@JvmInline
value class StyleWrapper(private val builder: Style.Builder) {
    fun build() = builder.build()

    fun black() = color(ColorPalette.BLACK)

    fun darkBlue() = color(ColorPalette.DARK_BLUE)

    fun darkGreen() = color(ColorPalette.DARK_GREEN)

    fun darkAqua() = color(ColorPalette.DARK_AQUA)

    fun darkRed() = color(ColorPalette.DARK_RED)

    fun darkPurple() = color(ColorPalette.DARK_PURPLE)

    fun gold() = color(ColorPalette.GOLD)

    fun gray() = color(ColorPalette.GRAY)

    fun darkGray() = color(ColorPalette.DARK_GRAY)

    fun blue() = color(ColorPalette.BLUE)

    fun green() = color(ColorPalette.GREEN)

    fun aqua() = color(ColorPalette.AQUA)

    fun red() = color(ColorPalette.RED)

    fun lightPurple() = color(ColorPalette.LIGHT_PURPLE)

    fun yellow() = color(ColorPalette.YELLOW)

    fun white() = color(ColorPalette.WHITE)

    fun color(color: RGB) = apply {
        builder.color(TextColor.color(color))
    }

    fun bold() = apply {
        builder.decorate(TextDecoration.BOLD)
    }

    fun underlined() = apply {
        builder.decorate(TextDecoration.UNDERLINED)
    }

    fun italic() = apply {
        builder.decorate(TextDecoration.ITALIC)
    }

    fun strikethrough() = apply {
        builder.decorate(TextDecoration.STRIKETHROUGH)
    }

    fun obfuscated() = apply {
        builder.decorate(TextDecoration.OBFUSCATED)
    }

    fun onClick(action: ClickEvent.Action, value: String) = apply {
        builder.clickEvent(ClickEvent.clickEvent(action, value))
    }

    val openUrl get() = ClickEvent.Action.OPEN_URL

    val openFile get() = ClickEvent.Action.OPEN_FILE

    val runCommand get() = ClickEvent.Action.RUN_COMMAND

    val suggestCommand get() = ClickEvent.Action.SUGGEST_COMMAND

    val changePage get() = ClickEvent.Action.CHANGE_PAGE

    val copyToClipboard get() = ClickEvent.Action.COPY_TO_CLIPBOARD

    fun <T : Any> onHover(action: HoverEvent.Action<T>, value: T) = apply {
        builder.hoverEvent(HoverEvent.hoverEvent(action, value))
    }

    val showText: HoverEvent.Action<Component> get() = HoverEvent.Action.SHOW_TEXT

    val showItem: HoverEvent.Action<HoverEvent.ShowItem> get() = HoverEvent.Action.SHOW_ITEM

    val showEntity: HoverEvent.Action<HoverEvent.ShowEntity> get() = HoverEvent.Action.SHOW_ENTITY

    fun insert(string: String) = apply {
        builder.insertion(string)
    }

    fun font(key: Key) = apply {
        builder.font(key)
    }
}