@file:JvmName("DF")

package io.github.homchom.recode.server

import io.github.homchom.recode.mc

private val dfIPRegex = Regex("""(?:\w+\.)?(?:mcdiamondfire\.(?:com|net)|luke.cash)(?::\d+)?""")

val isOnDF get() = mc.currentServer?.ip?.matches(dfIPRegex) ?: false