package io.github.homchom.recode.game

import kotlin.time.Duration.Companion.milliseconds

/**
 * @return this integer as a [kotlin.time.Duration] in ticks, where 20 ticks = 1 second.
 */
val Int.ticks get() = milliseconds * 50