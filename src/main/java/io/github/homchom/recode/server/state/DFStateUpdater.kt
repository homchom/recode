@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.server.*
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.matchAgainst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

val currentDFState by CurrentState

private val dfIPRegex = Regex("""(?:\w+\.)?mcdiamondfire\.com(?::\d+)?""")

val DFStateUpdater = module {
    onLoad {
        JoinServerEvent.hook { (_, _, client) ->
            if (dfIPRegex matches client.currentServer!!.ip) {
                coroutineScope.launch {
                    delay(100L) // TODO: remove (ViaVersion bug)
                    val node = LocateMessage.request(Case(null)).state.node
                    CurrentState.set(DFState.AtSpawn(node, false))
                }
            }
        }

        ReceiveChatMessageEvent.hook { message ->
            // Play, Build, and Dev Mode
            message.matchAgainst(PlotMode)?.let {
                CurrentState.locateAndSet(coroutineScope) { currentDFState!!.withState(it) }
            }
        }

        DisconnectFromServerEvent.hook {
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
        scope.launch {
            val locateState = LocateMessage.request(Case(null)).state
            set(setter(locateState))
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = dfState
}