package io.github.homchom.recode.util

import io.github.homchom.recode.util.regex.regex

private val humpRegex = regex {
    none("A-Z")
    any("A-Z")
}

/**
 * Splits this string into a list of words separated by "humps" (boundaries between a lowercase and uppercase
 * letter A-Z, as in camel case strings).
 */
fun String.splitByHumps() = buildList {
    var substringStart = 0
    for (match in humpRegex.findAll(this@splitByHumps)) {
        val next = match.range.last
        add(substring(substringStart, next))
        substringStart = next
    }
    add(substring(substringStart, length))
}