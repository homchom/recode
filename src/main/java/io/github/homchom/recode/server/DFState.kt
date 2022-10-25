@file:JvmName("DF")

package io.github.homchom.recode.server

import io.github.homchom.recode.mc

private val dfIpRegex = Regex("""\w+\.?mcdiamondfire\.com(?::\d+)?""")

val isOnDF get() = mc.currentServer?.ip?.matches(dfIpRegex) ?: false