@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.multiplayer.state

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.GroupListenable
import io.github.homchom.recode.event.StateListenable
import io.github.homchom.recode.event.filterIsInstance
import io.github.homchom.recode.event.trial.TrialScope
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.multiplayer.message.CodeMessages
import io.github.homchom.recode.multiplayer.message.StateMessages
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.ui.unstyle
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.encase
import io.github.homchom.recode.util.regex.namedGroupValues
import io.github.homchom.recode.util.regex.regex
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

private val eventGroup = GroupListenable<Case<DFState?>>()

// TODO: enable and disable JoinDFDetector and make this a child
object DFStateDetectors : StateListenable<Case<DFState?>> by eventGroup {
    private val scoreboardNodeRegex = Regex("""(?<node>.+) - .+""")

    private val teleportEvent = ReceiveGamePacketEvent.filterIsInstance<ClientboundPlayerPositionPacket>()

    val EnterSpawn = eventGroup.add(detector("spawn",
        trial(teleportEvent, Unit) { _, _ ->
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
        trial(JoinDFDetector, Unit) { info, _ ->
            suspending {
                val permissions = power.async {
                    val message = StateMessages.Profile.request(mc.player!!.username, true)
                    PermissionGroup(message.ranks)
                }

                Case(DFState.AtSpawn(info.node, permissions, null))
            }
        }
    ))

    val ChangeMode = eventGroup.add(detector("mode change",
        trial(ReceiveChatMessageEvent, Unit) { (message), _ ->
            enforce { requireTrue(isOnDF) }
            suspending {
                PlotMode.ID.match(message)?.encase { mode ->
                    val state = currentDFState!!.withState(locate()) as? DFState.OnPlot
                    if (state?.mode?.id != mode) fail()
                    state
                }
            }
        }
    ))

    val StartSession = eventGroup.add(detector("session start",
        trial(ReceiveChatMessageEvent, Unit) { (message), _ ->
            enforce { requireTrue(isOnDF) }

            requireTrue(currentDFState!!.session == null)
            val session = SupportSession.match(message)!!

            val subsequent = ReceiveChatMessageEvent.add()
            val enforceChannel = ReceiveChatMessageEvent.add()
            suspending {
                enforce(enforceChannel) { (text) -> SupportSession.match(text) == null }

                val regex = regex {
                    str("You have entered a session with ")
                    username()
                    period
                }
                // this is safe because of the previous enforce call; only one can run at a time
                testBoolean(subsequent, unlimited, Duration.INFINITE) { (text) ->
                    regex.matchesUnstyled(text)
                }

                val supportTime = CodeMessages.SupportTime.request(Unit, true).duration
                requireTrue(supportTime != null)
                Case(currentDFState!!.withSession(session))
            }
        }
    ))

    val EndSession = eventGroup.add(detector("session end",
        trial(ReceiveChatMessageEvent, Unit) { (message), _ ->
            enforce { requireTrue(isOnDF) }

            requireTrue(currentDFState!!.session != null)

            // TODO: is there a better way to do this with fewer false positives?
            val regex = regex {
                str("Your session with ")
                username()
                str(" has ended.")
            }

            requireTrue(regex.matchesUnstyled(message))
            instant(Case(currentDFState!!.withSession(null)))
        }
    ))

    val LeaveServer = eventGroup.add(detector("DF leave",
        trial(DisconnectFromServerEvent, Unit) { _, _ -> instant(Case.ofNull) }
    ))

    private val power = Power()

    init {
        power.extend(eventGroup)
    }

    private suspend fun TrialScope.locate() =
        mc.player?.run {
            val message = StateMessages.Locate.request(username, true)
            message.state
        } ?: fail()
}