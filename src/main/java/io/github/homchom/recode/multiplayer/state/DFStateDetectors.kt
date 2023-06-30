@file:JvmName("DFGlobals")
@file:JvmMultifileClass

package io.github.homchom.recode.multiplayer.state

import io.github.homchom.recode.event.*
import io.github.homchom.recode.game.TeleportEvent
import io.github.homchom.recode.lifecycle.GlobalModule
import io.github.homchom.recode.lifecycle.ModuleDetail
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.ui.unstyle
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.encase
import io.github.homchom.recode.util.namedGroupValues
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import kotlin.time.Duration

val currentDFState get() = DFStateDetectors.prevResult?.content

val isOnDF get() = currentDFState != null

@OptIn(DelicateCoroutinesApi::class)
private val stateModule = module(ModuleDetail.Exposed) {
    extend(GlobalModule)
}

// TODO: enable and disable JoinDFDetector and make this a child
object DFStateDetectors : StateListenable<Case<DFState?>>, RModule by stateModule {
    private val group = GroupListenable<Case<DFState?>>()

    private val scoreboardNodeRegex = Regex("""(?<node>.+) - .+""")

    val EnterSpawn = group.add(detector(
        nullaryTrial(TeleportEvent) { _ ->
            enforce { requireTrue(isOnDF) }

            val gameMenuStack = mc.player!!.inventory.getItem(4) // middle hotbar slot
            requireTrue("◇ Game Menu ◇" in gameMenuStack.hoverName.string)

            val scoreboard = mc.player!!.scoreboard
            val objective = scoreboard.getObjective("info")!!
            val score = scoreboard.getPlayerScores(objective).singleOrNull { it.score == 3 } ?: fail()
            val node = scoreboardNodeRegex.matchEntire(unstyle(score.owner))!!
                .namedGroupValues["node"]
                .let(::nodeByName)
            requireTrue(currentDFState !is SpawnState || node != currentDFState!!.node)

            val extraTeleport = TeleportEvent.add()
            suspending {
                // the player is teleported multiple times, so only detect the last one
                failOn(extraTeleport)

                val locateState = locate()
                requireTrue(locateState is SpawnState)
                Case(currentDFState!!.withState(locateState))
            }
        },
        nullaryTrial(JoinDFDetector) { info ->
            suspending {
                val permissions = stateModule.async {
                    val request = UserStateRequest(mc.player!!.username, true)
                    val message = ProfileMessage.request(request)
                    PermissionGroup(message.ranks)
                }

                Case(DFState.AtSpawn(info.node, permissions, null))
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

    val EnterSession = group.add(detector(nullaryTrial(ReceiveChatMessageEvent) { (message) ->
        enforce { requireTrue(isOnDF) }

        requireTrue(currentDFState!!.session == null)
        val session = SupportSession.match(message)!!.matcher

        val subsequent = ReceiveChatMessageEvent.add()
        suspending {
            enforce(subsequent) { (text) -> SupportSession.match(text) == null }

            val regex = Regex("""You have entered a session with $USERNAME_PATTERN.""")
            // this is safe because of the previous enforce call; only one can run at a time
            testBoolean(subsequent, unlimited, Duration.INFINITE) { (text) ->
                regex.matchesUnstyled(text)
            }

            requireTrue(SupportTimeRequester.request(true).content != null)
            Case(currentDFState!!.withSession(session))
        }
    }))

    val LeaveSession = group.add(detector(nullaryTrial(DFStateDetectors) { (state) ->
        enforce { requireTrue(isOnDF) }
        requireTrue(state?.session != null) // prevents recursion

        suspending {
            requireTrue(SupportTimeRequester.request(true).content == null)
            Case(currentDFState!!.withSession(null))
        }
    }))

    val LeaveServer = group.add(detector(
        nullaryTrial(DisconnectFromServerEvent) { instant(Case.ofNull) }
    ))

    override val prevResult get() = group.prevResult
    override fun getNotificationsFrom(module: RModule) = group.getNotificationsFrom(module)

    private suspend fun TrialScope.locate() =
        mc.player?.run {
            val message = LocateMessage.request(UserStateRequest(username, true))
            message.state
        } ?: fail()
}