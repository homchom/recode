@file:JvmName("Logging")

package io.github.homchom.recode

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(MOD_ID)

/**
 * @see org.slf4j.Logger.info
 */
fun logInfo(message: String) = logger.info("[$MOD_NAME] $message")

/**
 * @see org.slf4j.Logger.error
 */
@JvmOverloads
fun logError(message: String, mentionBugReport: Boolean = false) {
    val bugString = if (mentionBugReport) {
        "\nIf you believe this is a bug, you can report it here: https://github.com/homchom/recode/issues)"
    } else ""
    logger.error("[$MOD_NAME] $message$bugString")
}

/**
 * Uses [logInfo] to log a debug message if [debug] is `true`.
 */
fun logDebug(message: String) = logDebug { message }

/**
 * Uses [logInfo] to log a debug message if [debug] is `true`.
 */
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