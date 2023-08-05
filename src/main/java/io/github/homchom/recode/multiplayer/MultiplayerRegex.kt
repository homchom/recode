package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.util.regex.RegexPatternBuilder

fun RegexPatternBuilder.username(string: String? = null) =
    if (string == null) wordChar * (3..16) else str(string)