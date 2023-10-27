package io.github.homchom.recode.ui

import io.github.homchom.recode.render.ColorPalette
import io.github.homchom.recode.render.IntegralColor
import net.minecraft.network.chat.*
import net.minecraft.network.chat.HoverEvent.EntityTooltipInfo
import net.minecraft.network.chat.HoverEvent.ItemStackInfo
import net.minecraft.resources.ResourceLocation

typealias TextScope = TextBuilder.() -> Unit

/**
 * Creates a translated [Component] with [key], [args], and [style].
 */
fun translateText(
    key: String,
    style: StyleWrapper = style(),
    args: Array<out Any> = emptyArray()
): Component {
    return Component.translatable(key, *args).withStyle(style.result)
}

/**
 * Creates a [Component] with the literal [string] and [style].
 */
fun literalText(string: String, style: StyleWrapper = style()): Component =
    Component.literal(string).withStyle(style.result)

/**
 * Builds a [Component] by adding [style] to [root] and applying [builder].
 *
 * use [translateText] and [literalText] when applicable, as their output is more optimized.
 *
 * @see TextBuilder
 */
inline fun text(
    style: StyleWrapper = style(),
    root: MutableComponent = Component.empty(),
    builder: TextScope
): Component {
    return root.withStyle(style.result)
        .let(::TextBuilder)
        .apply(builder)
        .result
}

/**
 * Creates a [StyleWrapper].
 */
fun style(initial: Style = Style.EMPTY) = StyleWrapper(initial)

/**
 * A builder class for text [Component] objects.
 *
 * @see translate
 * @see literal
 */
@JvmInline
value class TextBuilder(val result: MutableComponent = Component.empty()) {
    /**
     * Appends [translateText] with [key], [args], and [style] and applies [builder] to it.
     */
    inline fun translate(
        key: String,
        style: StyleWrapper = style(),
        args: Array<out Any> = emptyArray(),
        builder: TextScope = {}
    ) {
        result.append(text(style, Component.translatable(key, *args), builder))
    }

    /**
     * Appends [literalText] with [string] and [style] and applies [builder] to it.
     */
    inline fun literal(
        string: String,
        style: StyleWrapper = style(),
        builder: TextScope = {}
    ) {
        result.append(text(style, Component.literal(string), builder))
    }
}

/**
 * A wrapper class for idiomatic [Style] creation.
 */
@Suppress("unused")
@JvmInline
value class StyleWrapper(val result: Style) {
    private inline fun map(transform: Style.() -> Style) = StyleWrapper(result.transform())

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

    fun color(color: Int) = map { withColor(color) }

    fun bold() = map { withBold(true) }

    fun underlined() = map { withUnderlined(true) }

    fun italic() = map { withItalic(true) }

    fun strikethrough() = map { withStrikethrough(true) }

    fun obfuscated() = map { withObfuscated(true) }

    fun onClick(action: ClickEvent.Action, value: String) =
        map { withClickEvent(ClickEvent(action, value)) }

    val openUrl get() = ClickEvent.Action.OPEN_URL

    val openFile get() = ClickEvent.Action.OPEN_FILE

    val runCommand get() = ClickEvent.Action.RUN_COMMAND

    val suggestCommand get() = ClickEvent.Action.SUGGEST_COMMAND

    val changePage get() = ClickEvent.Action.CHANGE_PAGE

    val copyToClipboard get() = ClickEvent.Action.COPY_TO_CLIPBOARD

    fun <T : Any> onHover(action: HoverEvent.Action<T>, value: T) =
        map { withHoverEvent(HoverEvent(action, value)) }

    val showText: HoverEvent.Action<Component> get() = HoverEvent.Action.SHOW_TEXT

    val showItem: HoverEvent.Action<ItemStackInfo> get() = HoverEvent.Action.SHOW_ITEM

    val showEntity: HoverEvent.Action<EntityTooltipInfo> get() = HoverEvent.Action.SHOW_ENTITY

    fun insert(string: String) = map { withInsertion(string) }

    fun font(id: ResourceLocation) = map { withFont(id) }
}