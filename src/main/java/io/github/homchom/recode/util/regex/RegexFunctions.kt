package io.github.homchom.recode.util.regex

/**
 * Gets the value of this MatchResult's group with name [name]. For consistency with [MatchResult.groupValues],
 * if a group with this name does not exist, an empty string is returned instead.
 */
fun MatchResult.groupValue(name: String) = groups[name]?.value ?: ""