package io.github.homchom.recode.ui.text

import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.render.IntegralColor
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.minecraft.util.FormattedCharSequence

/**
 * Builds a composite [FormattedCharSequence] by applying [builder].
 *
 * Use [text] for higher-level [Component] creation, which supports more than literals.
 *
 * @see FormattedCharSequenceBuilder
 */
inline fun formattedCharSequence(builder: FormattedCharSequenceBuilder.() -> Unit) =
    FormattedCharSequenceBuilder().apply(builder).build()

/**
 * Creates a [StyleWrapper].
 */
fun style(initial: Style = Style.empty()) = StyleWrapper(initial.toBuilder())

/**
 * @see forward
 * @see backward
 *
 * @see formattedCharSequence
 */
@JvmInline
value class FormattedCharSequenceBuilder private constructor(private val list: MutableList<FormattedCharSequence>) {
    constructor() : this(mutableListOf())

    fun build(): FormattedCharSequence = FormattedCharSequence.composite(list)

    /**
     * Appends [string] in forward order with [style].
     */
    fun forward(string: String, style: StyleWrapper) {
        list += FormattedCharSequence.forward(string, style.build().toVanilla())
    }

    /**
     * Appends [string] in backward order with [style].
     */
    fun backward(string: String, style: StyleWrapper) {
        list += FormattedCharSequence.backward(string, style.build().toVanilla())
    }

    /**
     * Appends the character with code point [code], with [style].
     */
    fun codepoint(code: Int, style: StyleWrapper) {
        list += FormattedCharSequence.codepoint(code, style.build().toVanilla())
    }
}

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

    fun color(color: IntegralColor) = color(color.toInt())

    fun color(color: Int) = apply {
        builder.color { color }
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