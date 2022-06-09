@file:JvmName("Constants")

package io.github.homchom.recode

import net.minecraft.resources.ResourceLocation

const val MOD_ID = "recode"
const val MOD_NAME = "recode"
const val ROOT_PACKAGE = "io.github.homchom.recode"
//const val FEATURE_PACKAGE = "$ROOT_PACKAGE.feature"

fun id(string: String) = ResourceLocation(MOD_ID, string)