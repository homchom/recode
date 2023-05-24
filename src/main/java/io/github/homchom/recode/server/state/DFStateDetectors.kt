@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.ItemSlotUpdateEvent
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.DisconnectFromServerEvent
import io.github.homchom.recode.server.JoinDFDetector
import io.github.homchom.recode.server.ReceiveChatMessageEvent
import io.github.homchom.recode.server.username
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.collections.immutable
import io.github.homchom.recode.util.encase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

val currentDFState get() = DFStateDetectors.prevResult.content

val isOnDF get() = currentDFState != null

private val stateModule = exposedModule()

// TODO: enable and disable JoinDFDetector and make this a child
object DFStateDetectors : StateListenable<Case<DFState?>>, RModule by stateModule {
    private val group = GroupListenable<Case<DFState?>>()

    private val event by lazy {
        group.getNotificationsFrom(stateModule)
            .stateIn(stateModule, SharingStarted.WhileSubscribed(), Case(null))
            .let { DependentResultListenable(it.asStateListenable(), stateModule) }
    }

    // TODO: auto /fly uses a ToggleRequesterGroup so it doesn't erroneously run twice, but this still can. fix
    val EnterSpawn = group.add(detector(
        nullaryTrial(ItemSlotUpdateEvent) { packet ->
            enforce {
                requireTrue(isOnDF && currentDFState !is SpawnState)
            }
            val stack = packet.item
            requireTrue("◇ Game Menu ◇" in stack.hoverName.string)
            val display = stack.orCreateTag.getCompound("display")
            val lore = display.getList("Lore", 8).toString()
            requireTrue("\"Click to open the Game Menu.\"" in lore)
            requireTrue("\"Hold and type in chat to search.\"" in lore)

            suspending {
                Case(currentDFState?.withState(locate()) ?: fail())
            }
        },
        nullaryTrial(JoinDFDetector) { info ->
            suspending {
                val permissions = stateModule.async {
                    val request = HideableStateRequest(mc.player?.username ?: fail(), true)
                    val message = +awaitBy(ProfileMessage, request)
                    PermissionGroup(message.ranks.immutable())
                }

                Case(DFState.AtSpawn(info.node, permissions))
            }
        }
    ))

    val ChangeMode = group.add(detector(nullaryTrial(ReceiveChatMessageEvent) { (message) ->
        enforce { requireTrue(isOnDF) }
        suspending {
            PlotMode.match(message)?.encase { match ->
                val state = currentDFState!!.withState(locate()) as? DFState.OnPlot
                if (state?.mode != match.matcher) fail()
                state
            }
        }
    }))

    val Leave = group.add(detector(
        nullaryTrial(DisconnectFromServerEvent) { instant(Case(null)) }
    ))

    override val prevResult get() = event.prevResult
    override fun getNotificationsFrom(module: RModule) = event.getNotificationsFrom(module)

    private suspend fun AsyncTrialScope.locate() =
        mc.player?.run {
            val message = +awaitBy(LocateMessage, HideableStateRequest(username, true))
            message.state
        } ?: fail()
}