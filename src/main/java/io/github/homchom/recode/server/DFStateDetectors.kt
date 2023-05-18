@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server

import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.ItemSlotUpdateEvent
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.message.LocateMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

val currentDFState get() = DFStateDetectors.prevResult

val isOnDF get() = currentDFState != null

private val module = exposedModule()

// TODO: enable and disable JoinDFDetector and make this a child
object DFStateDetectors : StateListenable<DFState?>, RModule by module {
    private val group = GroupListenable<DFState?>()

    private val event by lazy {
        group.getNotificationsFrom(module)
            .stateIn(module, SharingStarted.WhileSubscribed(), null)
            .let { DependentResultListenable(it.asStateListenable(), module) }
    }

    // TODO: auto /fly uses a ToggleRequesterGroup so it doesn't erroneously run twice, but this still can. fix
    val EnterSpawn = group.add(detector(
        nullaryTrial(ItemSlotUpdateEvent) { packet ->
            enforce {
                requireTrue(isOnDF && currentDFState !is DFState.AtSpawn)
            }
            val stack = packet.item
            requireTrue("◇ Game Menu ◇" in stack.hoverName.string)
            val display = stack.orCreateTag.getCompound("display")
            val lore = display.getList("Lore", 8).toString()
            requireTrue("\"Click to open the Game Menu.\"" in lore)
            requireTrue("\"Hold and type in chat to search.\"" in lore)

            suspending { DFState.AtSpawn(locate().node, /*false*/) }
        },
        nullaryTrial(JoinDFDetector) { (node) ->
            instant(DFState.AtSpawn(node, /*false*/))
        }
    ))

    val ChangeMode = group.add(detector(nullaryTrial(ReceiveChatMessageEvent) { (message) ->
        enforce { requireTrue(isOnDF) }
        suspending {
            PlotMode.match(message)?.let { match ->
                val state = currentDFState!!.withState(locate()) as? DFState.OnPlot
                if (state?.mode != match.matcher) fail()
                state
            }
        }
    }))

    val Leave = group.add(detector(nullaryTrial(DisconnectFromServerEvent) { instant(null) }))

    override val prevResult get() = event.prevResult

    override fun getNotificationsFrom(module: RModule) = event.getNotificationsFrom(module)

    private suspend fun AsyncTrialScope.locate() =
        mc.player?.run {
            val message = +awaitBy(LocateMessage, LocateMessage.Request(username, true))
            message.state
        } ?: fail()
}