package io.github.homchom.recode.util.coroutines

import io.github.homchom.recode.mc
import kotlinx.coroutines.asCoroutineDispatcher

/**
 * A [kotlinx.coroutines.CoroutineDispatcher] that dispatches to Minecraft's main thread.
 */
val MinecraftDispatcher = mc.asCoroutineDispatcher()