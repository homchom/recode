@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.event.*
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.*
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.encase
import io.github.homchom.recode.util.unitOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

val currentDFState get() = DFStateDetector.currentState

object DFStateDetector : StateListenable<DFState?>, ExposedModule by exposedModule() {
    private val group = GroupListenable<Case<DFState>>()

    val Join = group.add(nullaryDetector(JoinServerEvent) {
        isOnDF.unitOrNull()?.encase {
            delay(200L) // TODO: remove (ViaVersion bug)
            DFState.AtSpawn(requestLocate().node, false)
        }
    })

    val ChangeMode = group.add(nullaryDetector(ReceiveChatMessageEvent) { message ->
        PlotMode.match(message)?.encase { currentDFState!!.withState(requestLocate()) }
    })

    val Leave = group.add(nullaryDetector(DisconnectFromServerEvent) { Case(null) })

    @Deprecated("Only for Java use")
    val Legacy = group.add(createEvent())

    override val notifications: Flow<DFState?> get() = stateFlow
    override val currentState get() = stateFlow.value

    private val stateFlow by lazy {
        group.notifications.map { it.content }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)
    }
}

private suspend fun requestLocate() = LocateMessage.request(mc.player!!.username).state