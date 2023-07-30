@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.multiplayer.state

import io.github.homchom.recode.event.GroupListenable
import io.github.homchom.recode.event.StateListenable
import io.github.homchom.recode.event.filterIsInstance
import io.github.homchom.recode.event.trial.TrialScope
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.nullaryTrial
import io.github.homchom.recode.lifecycle.GlobalModule
import io.github.homchom.recode.lifecycle.RModule
import io.github.homchom.recode.lifecycle.module
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.multiplayer.event.LocateMessage
import io.github.homchom.recode.multiplayer.event.ProfileMessage
import io.github.homchom.recode.multiplayer.event.SupportTimeRequester
import io.github.homchom.recode.multiplayer.event.UserStateRequest
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.ui.unstyle
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.encase
import io.github.homchom.recode.util.namedGroupValues
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.async
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import kotlin.time.Duration

/**
 * The current [DFState].
 *
 * This updates after all [DFStateDetectors], so it can be used as the "old" state in listeners. New states are
 * always passed as context to the detectors.
 */
val currentDFState get() = DFStateDetectors.previous.value?.content

val isOnDF get() = currentDFState != null

// TODO: enable and disable JoinDFDetector and make this a child
object DFStateDetectors : StateListenable<Case<DFState?>> {
    private val group = GroupListenable<Case<DFState?>>()

    private val scoreboardNodeRegex = Regex("""(?<node>.+) - .+""")

    private val teleportEvent = ReceiveGamePacketEvent.filterIsInstance<ClientboundPlayerPositionPacket>()

    val EnterSpawn = group.add(detector("spawn",
        nullaryTrial(teleportEvent) { _ ->
            enforce { requireTrue(isOnDF) }

            val gameMenuStack = mc.player!!.inventory.getItem(4) // middle hotbar slot
            requireTrue("◇ Game Menu ◇" in gameMenuStack.hoverName.string)

            val scoreboard = mc.player!!.scoreboard
            val objective = scoreboard.getObjective("info")!!
            val score = scoreboard.getPlayerScores(objective).singleOrNull { it.score == 3 } ?: fail()
            val node = scoreboardNodeRegex.matchEntire(unstyle(score.owner))!!
                .namedGroupValues["node"]
                .let(::nodeByName)
            requireTrue(currentDFState !is DFState.AtSpawn || node != currentDFState!!.node)

            val extraTeleport = teleportEvent.add()
            suspending {
                // the player is teleported multiple times, so only detect the last one
                failOn(extraTeleport)

                val locateState = locate()
                requireTrue(locateState is LocateState.AtSpawn)
                Case(currentDFState!!.withState(locateState))
            }
        },
        nullaryTrial(JoinDFDetector) { info ->
            suspending {
                val permissions = module.async {
                    val request = UserStateRequest(mc.player!!.username, true)
                    val message = ProfileMessage.request(request)
                    PermissionGroup(message.ranks)
                }

                Case(DFState.AtSpawn(info.node, permissions, null))
            }
        }
    ))

    val ChangeMode = group.add(detector("mode change",
        nullaryTrial(ReceiveChatMessageEvent) { (message) ->
            enforce { requireTrue(isOnDF) }
            suspending {
                PlotMode.ID.match(message)?.encase { match ->
                    val state = currentDFState!!.withState(locate()) as? DFState.OnPlot
                    if (state?.mode?.id != match.matcher) fail()
                    state
                }
            }
        }
    ))

    val StartSession = group.add(detector("session start",
        nullaryTrial(ReceiveChatMessageEvent) { (message) ->
            enforce { requireTrue(isOnDF) }

            requireTrue(currentDFState!!.session == null)
            val session = SupportSession.match(message)!!.matcher

            val subsequent = ReceiveChatMessageEvent.add()
            val enforceChannel = ReceiveChatMessageEvent.add()
            suspending {
                enforce(enforceChannel) { (text) -> SupportSession.match(text) == null }

                val regex = Regex("""You have entered a session with $USERNAME_PATTERN\.""")
                // this is safe because of the previous enforce call; only one can run at a time
                testBoolean(subsequent, unlimited, Duration.INFINITE) { (text) ->
                    regex.matchesUnstyled(text)
                }

                requireTrue(SupportTimeRequester.request(true).content != null)
                Case(currentDFState!!.withSession(session))
            }
        }
    ))

    val EndSession = group.add(detector("session end",
        nullaryTrial(ReceiveChatMessageEvent) { (message) ->
            enforce { requireTrue(isOnDF) }

            requireTrue(currentDFState!!.session != null)

            // TODO: is there a better way to do this with fewer false positives?
            val regex = Regex("""Your session with $USERNAME_PATTERN has ended\.""")
            requireTrue(regex.matchesUnstyled(message))
            instant(Case(currentDFState!!.withSession(null)))
        }
    ))

    val LeaveServer = group.add(detector("DF leave",
        nullaryTrial(DisconnectFromServerEvent) { instant(Case.ofNull) }
    ))

    @OptIn(DelicateCoroutinesApi::class)
    private val module = module(group) { extend(GlobalModule) }

    override val previous get() = module.previous

    override fun getNotificationsFrom(module: RModule) = this.module.getNotificationsFrom(module)

    private suspend fun TrialScope.locate() =
        mc.player?.run {
            val message = LocateMessage.request(UserStateRequest(username, true))
            message.state
        } ?: fail()
}