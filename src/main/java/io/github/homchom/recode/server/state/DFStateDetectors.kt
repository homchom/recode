@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.server.state

import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.TeleportEvent
import io.github.homchom.recode.game.UpdateScoreboardScoreEvent
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.exposedModule
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.*
import io.github.homchom.recode.ui.unstyle
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.collections.immutable
import io.github.homchom.recode.util.encase
import io.github.homchom.recode.util.namedGroupValues
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

    private val scoreboardNodeRegex = Regex("""(?<node>.+) - .+""")

    val EnterSpawn = group.add(detector(
        // leave plot
        nullaryTrial(TeleportEvent) { _ ->
            println("on teleport event")
            enforce { requireTrue(isOnDF) }

            val scoreboard = mc.player!!.scoreboard
            val objective = scoreboard.getObjective("info")!!
            val score = scoreboard.getPlayerScores(objective).singleOrNull { it.score == 3 } ?: fail()

            val node = scoreboardNodeRegex.matchEntire(unstyle(score.owner))!!
                .namedGroupValues["node"]
                .let(::nodeByName)
            requireTrue(currentDFState !is SpawnState || node != currentDFState!!.node)

            suspending {
                val locateState = locate()
                requireTrue(locateState is SpawnState)
                Case(currentDFState!!.withState(locateState))
            }
        },
        nullaryTrial(JoinDFDetector) { info ->
            suspending {
                val permissions = stateModule.async {
                    val request = HideableStateRequest(mc.player!!.username, true)
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