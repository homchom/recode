@file:JvmName("BasicTypeExtensions")

package io.github.homchom.recode.util

// booleans

/**
 * @return [Unit] if this boolean is true, or `null` otherwise.
 */
fun Boolean.unitOrNull() = if (this) Unit else null

// strings

/**
 * Appends multiple [substrings] to this [StringBuilder].
 */
fun StringBuilder.interpolate(vararg substrings: String) = apply {
    for (substring in substrings) append(substring)
}