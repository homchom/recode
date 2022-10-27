@file:JvmName("LegacyCoroutineFunctions")

package io.github.homchom.recode.util

import io.github.homchom.recode.logError
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.sys.player.DFInfo
import kotlinx.coroutines.*

// TODO: replace with trials system
@DelicateCoroutinesApi
@Deprecated("Added as a stopgap in MMessageListener but should not be used and will be removed")
fun checkTwiceForMode(mode: LegacyState.Mode, action: Runnable) =
    GlobalScope.launch(Dispatchers.IO) {
        suspend fun check(d1: Long, d2: Long): Boolean {
            delay(d1)
            if (DFInfo.currentState.mode != mode) DFInfo.currentState.sendLocate()
            delay(d2)
            val match = DFInfo.currentState.mode == mode
            if (match) action.run()
            return match
        }

        try {
            if (!check(100L, 100L)) check(1000L, 400L)
        } catch (e: Throwable) {
            logError("Error while executing the task!")
            e.printStackTrace()
        }
}