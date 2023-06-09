@file:JvmName("BasicTypeExtensions")

package io.github.homchom.recode.util

fun Boolean.unitOrNull() = if (this) Unit else null

fun String.capitalize() = replaceFirstChar(Char::titlecase)
fun String.uncapitalize() = replaceFirstChar(Char::lowercase)

fun String.flatcase() = replace(" ", "").lowercase()