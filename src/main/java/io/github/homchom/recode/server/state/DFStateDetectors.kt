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

val currentDFState get() = DFStateDetectors.currentState.content

private val module = exposedModule()

object DFStateDetectors : StateListenable<Case<DFState?>>, ExposedModule by module {
    private val group = GroupListenable<Case<DFState?>>()

    private val event by lazy {
        group.getNotificationsFrom(this)
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), Case(null))
            .let { DependentStateListenable(StateFlowListenable(it), module) }
    }

    val Join = group.add(nullaryDetector(JoinServerEvent) {
        isOnDF.unitOrNull()?.encase {
            delay(200L) // TODO: remove (ViaVersion bug)
            DFState.AtSpawn(requestLocate().node, false)
        }
    })

    val ChangeMode = group.add(nullaryDetector(ReceiveChatMessageEvent) { message ->
        PlotMode.match(message)?.encase {
            currentDFState!!.withState(requestLocate()) as? DFState.OnPlot ?: fail()
        }
    })

    val Leave = group.add(nullaryDetector(DisconnectFromServerEvent) { Case(null) })

    @Deprecated("Only for Java use")
    val Legacy = group.add(createEvent())

    override val currentState get() = event.currentState

    override fun getNotificationsFrom(module: ExposedModule) = event.getNotificationsFrom(module)
}

private suspend fun requestLocate() = LocateMessage.request(mc.player!!.username).state