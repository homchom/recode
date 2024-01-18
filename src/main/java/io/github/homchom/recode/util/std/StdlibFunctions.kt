@file:JvmName("BasicTypeExtensions")

package io.github.homchom.recode.util.std

// booleans

/**
 * @return [Unit] if this boolean is true, or `null` otherwise.
 */
fun Boolean.unitOrNull() = if (this) Unit else null

// strings

/**
 * @see Character.toString
 */
fun String.Companion.fromCodePoint(codePoint: Int): String = Character.toString(codePoint)

/**
 * Appends multiple [substrings] to this [StringBuilder].
 */
fun StringBuilder.interpolate(vararg substrings: String) = apply {
    for (substring in substrings) append(substring)
}