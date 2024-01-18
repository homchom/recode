@file:JvmName("ModConstants")

package io.github.homchom.recode

import net.minecraft.resources.ResourceLocation
import kotlin.time.Duration.Companion.seconds

const val MOD_ID = "recode"
const val MOD_NAME = "recode"

const val MOD_LOGO_CHAR = "\udb9c\ude65"

const val ROOT_PACKAGE = "io.github.homchom.recode"
//const val FEATURE_PACKAGE = "$ROOT_PACKAGE.feature"

// TODO: add this as config setting (in some form)
val DEFAULT_TIMEOUT_DURATION = 60.seconds

// the Application ID given to recode by the Discord Developer Portal
const val DISCORD_APP_ID = 1105614492534059020

fun id(string: String) = ResourceLocation(MOD_ID, string)