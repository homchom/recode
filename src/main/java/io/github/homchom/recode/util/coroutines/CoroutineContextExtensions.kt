package io.github.homchom.recode.util.coroutines

import io.github.homchom.recode.logDebug
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * @see cancel
 * @see logDebug
 */
fun CoroutineScope.cancelAndLog(message: String) {
    logDebug(message)
    cancel(message)
}