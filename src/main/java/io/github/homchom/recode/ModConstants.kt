@file:JvmName("Constants")

package io.github.homchom.recode

import net.minecraft.resources.ResourceLocation
import kotlin.time.Duration.Companion.minutes

const val MOD_ID = "recode"
const val MOD_NAME = "recode"
const val ROOT_PACKAGE = "io.github.homchom.recode"
//const val FEATURE_PACKAGE = "$ROOT_PACKAGE.feature"

// TODO: add this as config setting
val DEFAULT_TIMEOUT_DURATION = 2.minutes

fun id(string: String) = ResourceLocation(MOD_ID, string)