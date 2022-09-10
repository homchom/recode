package io.github.homchom.recode.util

fun String.capitalize() = replaceFirstChar(Char::titlecase)
fun String.uncapitalize() = replaceFirstChar(Char::lowercase)

fun String.flatcase() = replace(" ", "").lowercase()