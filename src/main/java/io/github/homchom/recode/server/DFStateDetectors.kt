@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server

import io.github.homchom.recode.event.*
import io.github.homchom.recode.lifecycle.ExposedModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.message.LocateMessage
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.encase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

val currentDFState get() = DFStateDetectors.currentState.content

val isOnDF get() = currentDFState != null

private val module = exposedModule()

object DFStateDetectors : StateListenable<Case<DFState?>>, ExposedModule by module {
    private val group = GroupListenable<Case<DFState?>>()

    private val event by lazy {
        group.getNotificationsFrom(this)
            .stateIn(this, SharingStarted.WhileSubscribed(), Case(null))
            .let { DependentStateListenable(it.asStateListenable(), module) }
    }

    val EnterSpawn = group.add(detector(
        nullaryTrial(SendCommandEvent) { (command) ->
            // TODO: use an inventory event instead
            if (command != "spawn" && command != "s" && command != "leave") fail()
            Case(DFState.AtSpawn(locate().node, false))
        },
        nullaryTrial(JoinDFDetector) { (node) ->
            Case(DFState.AtSpawn(node, false))
        }
    ))

    val ChangeMode = group.add(detector(nullaryTrial(ReceiveChatMessageEvent) { (message) ->
        PlotMode.match(message)?.encase { match ->
            val state = currentDFState!!.withState(locate()) as? DFState.OnPlot
            if (state?.mode != match.matcher) fail()
            state
        }
    }))

    val Leave = group.add(detector(nullaryTrial(DisconnectFromServerEvent) { Case(null) }))

    @Deprecated("Only for Java use")
    val Legacy = group.add(createEvent())

    override val currentState get() = event.currentState

    override fun getNotificationsFrom(module: ExposedModule) = event.getNotificationsFrom(module)

    private suspend fun TrialScope.locate() =
        mc.player?.run { (+awaitBy(LocateMessage, username)).state } ?: fail()
}