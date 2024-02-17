@file:JvmName("Color")

package io.github.homchom.recode.render

import net.kyori.adventure.util.RGBLike
import net.minecraft.ChatFormatting

private interface RGBColor {
    val hex: Int

    fun red() = hex shr 16 and 255
    fun green() = hex shr 8 and 255
    fun blue() = hex and 255
}

/**
 * An RGB [hex] color.
 *
 * @property red
 * @property green
 * @property blue
 */
@JvmInline
value class RGB(override val hex: Int) : RGBColor, RGBLike {
    constructor(red: Int, green: Int, blue: Int) : this((red shr 16) + (green shr 8) + blue) {
        fun requireInRange(value: Int) =
            require(value in 0..255) { "RGB color channel values must be between 0 and 255" }
        requireInRange(red)
        requireInRange(green)
        requireInRange(blue)
    }

    override fun red() = super.red()
    override fun green() = super.green()
    override fun blue() = super.blue()
}

/**
 * An RGBA [hex] color, for renders that support transparency.
 *
 * @property red
 * @property green
 * @property blue
 * @property alpha
 */
@JvmInline
value class RGBA(override val hex: Int) : RGBColor {
    fun alpha() = hex shr 24 and 0xff

    constructor(
        red: Int,
        green: Int,
        blue: Int,
        alpha: Int = 255
    ) : this((alpha shr 24) + (red shr 16) + (green shr 8) + blue) {
        fun requireInRange(value: Int) =
            require(value in 0..255) { "RGB color channel values must be between 0 and 255" }
        requireInRange(red)
        requireInRange(green)
        requireInRange(blue)
        requireInRange(alpha)
    }
}

/**
 * A group of standard colors used by the mod, including built-in color codes.
 */
// TODO: add DF palette colors?
@Suppress("unused")
object ColorPalette {
    val BLACK get() = builtIn(ChatFormatting.BLACK)
    val DARK_BLUE get() = builtIn(ChatFormatting.DARK_BLUE)
    val DARK_GREEN get() = builtIn(ChatFormatting.DARK_GREEN)
    val DARK_AQUA get() = builtIn(ChatFormatting.DARK_AQUA)
    val DARK_RED get() = builtIn(ChatFormatting.DARK_RED)
    val DARK_PURPLE get() = builtIn(ChatFormatting.DARK_PURPLE)
    val GOLD get() = builtIn(ChatFormatting.GOLD)
    val GRAY get() = builtIn(ChatFormatting.GRAY)
    val DARK_GRAY get() = builtIn(ChatFormatting.DARK_GRAY)
    val BLUE get() = builtIn(ChatFormatting.BLUE)
    val GREEN get() = builtIn(ChatFormatting.GREEN)
    val AQUA get() = builtIn(ChatFormatting.AQUA)
    val RED get() = builtIn(ChatFormatting.RED)
    val LIGHT_PURPLE get() = builtIn(ChatFormatting.LIGHT_PURPLE)
    val YELLOW get() = builtIn(ChatFormatting.YELLOW)
    val WHITE get() = builtIn(ChatFormatting.WHITE)

    private fun builtIn(code: ChatFormatting) = RGB(code.color!!)
}