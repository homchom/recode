@file:JvmName("DF")
@file:JvmMultifileClass

package io.github.homchom.recode.hypercube.state

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.GroupListenable
import io.github.homchom.recode.event.StateListenable
import io.github.homchom.recode.event.filterIsInstance
import io.github.homchom.recode.event.trial.TrialScope
import io.github.homchom.recode.event.trial.TrialScopeException
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.hypercube.JoinDFDetector
import io.github.homchom.recode.hypercube.message.CodeMessages
import io.github.homchom.recode.hypercube.message.StateMessages
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.DisconnectFromServerEvent
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.ReceiveGamePacketEvent
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.text.LegacyCodeRemover
import io.github.homchom.recode.ui.text.matchesPlain
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.regex.groupValue
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

object DFStateDetectors : StateListenable<Case<DFState?>> by eventGroup {
    private val scoreboardNodeRegex = Regex("""(?<node>.+) - .+""")

    private val teleportEvent = ReceiveGamePacketEvent.filterIsInstance<ClientboundPlayerPositionPacket>()

    val EnterSpawn = eventGroup.add(detector("spawn",
        trial(teleportEvent, Unit) t@{ _, _ ->
            enforceOnDF()

            val gameMenuStack = mc.player!!.inventory.getItem(4) // middle hotbar slot
            if ("◇ Game Menu ◇" !in gameMenuStack.hoverName.string) return@t null

            val scoreboardText = run {
                val scoreboard = mc.player!!.scoreboard
                val objective = scoreboard.getObjective("info")!!
                val score = scoreboard.getPlayerScores(objective).singleOrNull { it.score == 3 }
                    ?: return@t null
                LegacyCodeRemover.removeCodes(score.owner)
            }
            val node = scoreboardNodeRegex.matchEntire(scoreboardText)!!
                .groupValue("node")
                .let(::nodeByName)
            if (currentDFState is DFState.AtSpawn && node == currentDFState?.node) {
                return@t null
            }

            val extraTeleport = teleportEvent.add()
            suspending s@{
                // the player is teleported multiple times, so only detect the last one
                failOn(extraTeleport)

                val locateState = locate() ?: return@s null
                val state = currentDFState!!.withState(locateState) as? DFState.AtSpawn
                    ?: return@s null
                Case(state)
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
        trial(ReceiveChatMessageEvent, Unit) t@{ (message), _ ->
            enforceOnDF()
            val (mode, plotName, plotOwner) = PlotMode.ID.match(message) ?: return@t null
            suspending s@{
                val locateState = locate() ?: return@s null
                val state = currentDFState!!.withState(locateState) as? DFState.OnPlot
                    ?: return@s null

                // checks to prevent plots from falsifying state
                if (state.mode.id != mode) return@s null
                if (plotName != null && state.plot.name != plotName) return@s null
                if (plotOwner != null && state.plot.owner != plotOwner) return@s null

                Case(state)
            }
        }
    ))

    val JoinEventNode = eventGroup.add(detector("event node",
        trial(ReceiveChatMessageEvent, Unit) t@{ (message), _ ->
            enforceOnDF()
            PlotMode.Play.match(message) ?: return@t null
            suspending s@{
                val state = currentDFState!!.withState(locate() ?: return@s null)
                if (state.node != Node.EVENT) return@s null
                Case(state)
            }
        }
    ))

    val StartSession = eventGroup.add(detector("session start",
        trial(ReceiveChatMessageEvent, Unit) t@{ (message), _ ->
            enforceOnDF()

            if (currentDFState!!.session != null) return@t null
            val session = SupportSession.match(message) ?: return@t null

            val subsequent = ReceiveChatMessageEvent.add()
            val enforceChannel = ReceiveChatMessageEvent.add()
            suspending s@{
                enforce(enforceChannel) { (text) -> SupportSession.match(text) == null }

                val regex = regex {
                    str("You have entered a session with ")
                    username()
                    period
                }
                // this is safe because of the previous enforce call; only one can run at a time
                testBoolean(subsequent, unlimited, Duration.INFINITE) { (text) ->
                    regex.matchesPlain(text)
                }

                CodeMessages.SupportTime.request(Unit, true).duration ?: return@s null
                Case(currentDFState!!.withSession(session))
            }
        }
    ))

    val EndSession = eventGroup.add(detector("session end",
        trial(ReceiveChatMessageEvent, Unit) t@{ (message), _ ->
            enforceOnDF()

            if (currentDFState!!.session == null) return@t null

            // TODO: is there a better way to do this with fewer false positives?
            val regex = regex {
                str("Your session with ")
                username()
                str(" has ended.")
            }

            if (!regex.matchesPlain(message)) return@t null
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

    // TODO: leverage Power to remove all occurrences of this
    private fun TrialScope.enforceOnDF() = enforce { if (!isOnDF) throw TrialScopeException() }

    private suspend fun locate() = mc.player?.let { player ->
        StateMessages.Locate.request(player.username, true).state
    }
}