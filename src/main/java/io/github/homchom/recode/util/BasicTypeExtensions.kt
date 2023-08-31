@file:JvmName("BasicTypeExtensions")

package io.github.homchom.recode.util

fun Boolean.unitOrNull() = if (this) Unit else null