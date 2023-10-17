@file:JvmName("Color")

package io.github.homchom.recode.render

import net.minecraft.ChatFormatting
import java.util.*

fun rgb(red: Int, green: Int, blue: Int) = RGBColor(red, green, blue)
fun rgba(red: Int, green: Int, blue: Int, alpha: Int = 255) = RGBAColor(red, green, blue, alpha)

/**
 * Converts this [Int] into a [HexColor].
 */
fun Int.toColor() = HexColor(this)

sealed interface IntegralColor {
    fun toInt(): Int
}

private sealed interface RGBIntegralColor : IntegralColor {
    val red: Int
    val green: Int
    val blue: Int

    override fun toInt() = (red shl 16) + (green shl 8) + blue
}

data class RGBColor(
    override val red: Int,
    override val green: Int,
    override val blue: Int
) : RGBIntegralColor

data class RGBAColor(
    override val red: Int,
    override val green: Int,
    override val blue: Int,
    val alpha: Int
) : RGBIntegralColor

@JvmInline
value class HexColor(val hex: Int) : IntegralColor {
    override fun toInt() = hex

    override fun toString() = String.format(Locale.US, "#%06x", hex)
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

    private fun builtIn(code: ChatFormatting) = code.color!!.toColor()
}