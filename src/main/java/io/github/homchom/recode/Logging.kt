@file:JvmName("Logging")

package io.github.homchom.recode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

private val logger = LoggerFactory.getLogger(MOD_ID).apply { isEnabledForLevel(Level.DEBUG) }

fun logInfo(message: String) = logger.info("[$MOD_NAME] $message")

@JvmOverloads
fun logError(message: String, mentionBugReport: Boolean = false) {
    val bugString = if (mentionBugReport) {
        "\nIf you believe this is a bug, you can report it here: https://github.com/homchom/recode/issues)"
    } else ""
    logger.error("[$MOD_NAME] $message$bugString")
}

fun logDebug(message: String) = logDebug { message }

inline fun logDebug(lazyMessage: () -> String) {
    if (debug) logInfo("[debug] ${lazyMessage()}")
}

/**
 * @see cancel
 * @see logDebug
 */
fun CoroutineScope.cancelAndLog(message: String) {
    logDebug(message)
    cancel(message)
}