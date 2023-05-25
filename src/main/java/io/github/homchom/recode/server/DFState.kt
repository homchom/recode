@file:JvmName("DF")

package io.github.homchom.recode.server

import io.github.homchom.recode.mc

private val dfIPRegex = Regex("""(?:\w+\.)?mcdiamondfire\.com(?::\d+)?""")
private val dfIPsRegex = Regex("""(\w+\.)?(?:mcdiamondfire\.(?:com|net)|luke.cash)(:\d+)?""")

val isOnDF get() = mc.currentServer?.ip?.matches(dfIPRegex) ?: false

fun fixDfIp(ip: String): String {
    if (ip.matches(dfIPsRegex)) {
        // first and second capture groups are any subdomain to ip & port respectively
        return ip.replace(dfIPsRegex, "$1mcdiamondfire.com$2")
    }
    return ip
}