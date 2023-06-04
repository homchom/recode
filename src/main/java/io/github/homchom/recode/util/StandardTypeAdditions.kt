@file:JvmName("BasicTypeExtensions")

package io.github.homchom.recode.util

fun Boolean.unitOrNull() = if (this) Unit else null

fun String.capitalize() = replaceFirstChar(Char::titlecase)
fun String.uncapitalize() = replaceFirstChar(Char::lowercase)

fun String.flatcase() = replace(" ", "").lowercase()

/**
 * Subtypes of this interface expect to be hashable as keys of any applicable collection, e.g. [Map] and [Set].
 */
interface KeyHashable {
    override operator fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}