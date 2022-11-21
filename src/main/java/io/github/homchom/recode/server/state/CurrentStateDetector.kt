@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.server.ChangeDFStateEvent
import io.github.homchom.recode.server.Message
import io.github.homchom.recode.server.ReceiveChatMessageEvent
import io.github.homchom.recode.server.StateChange
import io.github.homchom.recode.util.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

var currentDFState: DFState? = null
    private set(value) {
        if (value != field) ChangeDFStateEvent.run(StateChange(value, field))
        field = value
    }

val CurrentStateDetector = module {
    val mutex = Mutex()

    onLoad {
        ReceiveChatMessageEvent.hook { (message) ->
            // Play, Build, and Dev Mode
            if (message() is Message.EnterPlotMode) coroutineScope.launch {
                val newState = LocateRequest.send(null).state
                mutex.withLock {
                    currentDFState = currentDFState!!.withState(newState)
                }
            }
        }
    }
}