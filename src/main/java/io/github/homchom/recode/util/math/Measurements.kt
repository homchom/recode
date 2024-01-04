package io.github.homchom.recode.util.math

import kotlin.time.Duration

/**
 * Creates a [Frequency].
 */
infix fun Int.per(interval: Duration) = Frequency(this, interval)

/**
 * A data structure that represents a frequency of [occurrences] occurrences per [interval].
 */
data class Frequency(val occurrences: Int, val interval: Duration)