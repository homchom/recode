@file:JvmName("Color")

package io.github.homchom.recode.render

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

data class HexColor(val hex: Int) : IntegralColor {
    override fun toInt() = hex
}