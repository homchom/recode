@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.*
import io.github.homchom.recode.util.matchAgainst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val currentDFState by CurrentState

val DFStateUpdater = module {
    onLoad {
        JoinServerEvent.listen {
            if (isOnDF) {
                coroutineScope.launch {
                    delay(200L) // TODO: remove (ViaVersion bug)
                    val node = requestLocate().node
                    CurrentState.set(DFState.AtSpawn(node, false))
                }
            }
        }

        ReceiveChatMessageEvent.listen { message ->
            // Play, Build, and Dev Mode
            message.matchAgainst(PlotMode)?.let {
                CurrentState.locateAndSet(coroutineScope) { currentDFState!!.withState(it) }
            }
        }

        DisconnectFromServerEvent.listen {
            CurrentState.setWithoutLock(null)
        }
    }
}

private object CurrentState : ReadOnlyProperty<Any?, DFState?> {
    private var dfState: DFState? = null
        set(value) {
            if (value != field) ChangeDFStateEvent.run(StateChange(value, field))
            field = value
        }

    private val mutex = Mutex()

    suspend fun set(state: DFState?) = mutex.withLock { setWithoutLock(state) }

    fun setWithoutLock(state: DFState?) {
        dfState = state
    }

    inline fun locateAndSet(scope: CoroutineScope, crossinline setter: (LocateState) -> DFState) {
        scope.launch { set(setter(requestLocate())) }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = dfState
}

private suspend fun requestLocate() = LocateMessage.request(mc.player!!.username).state