package io.github.homchom.recode.hypercube

import io.github.homchom.recode.Power
import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.listenEach
import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.hypercube.state.DFStateDetectors
import io.github.homchom.recode.multiplayer.ReceiveMessageEvent
import io.github.homchom.recode.multiplayer.SendPacketEvent
import io.github.homchom.recode.multiplayer.Sender
import io.github.homchom.recode.ui.text.equalsPlain
import io.github.homchom.recode.ui.text.matchesPlain
import io.github.homchom.recode.ui.text.plainText
import io.github.homchom.recode.util.regex.regex
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket

// TODO: replace more requesters with senders and update documentation
object CommandSenders {
    val ChatLocal = requester("/chat local", DFStateDetectors, trial(
        ReceiveMessageEvent.Chat,
        Unit,
        start = { Sender.sendCommand("chat local") },
        tests = { (text), _, _ ->
            val message = "$MAIN_ARROW Chat is now set to Local. You will only see messages from players on " +
                    "your plot. Use /chat to change it again."
            text.equalsPlain(message).instantUnitOrNull()
        }
    ))

    // TODO: support time keywords through command suggestions (not enum)
    val ClientTime = Sender(DFStateDetectors) { time: Long ->
        Sender.sendCommand("time $time")
    }

    private val lsRegex = regex {
        str(LAGSLAYER_PREFIX); space
        group {
            str("Now monitoring plot ")
            digit.oneOrMore()
            str(". Type /lagslayer to stop monitoring.")

            or

            str("Stopped monitoring plot ")
            digit.oneOrMore()
            period
        }
    }

    private val lsDelegate = requester("/lagslayer", DFStateDetectors, trial(
        ReceiveMessageEvent.Chat,
        Unit,
        start = { Sender.sendCommand("lagslayer") },
        tests = t@{ (message), _, _ ->
            lsRegex.matchesPlain(message).instantUnitOrNull()
        }
    ))

    object LagSlayer : Requester<Unit, Unit> by lsDelegate {
        var isLagSlayerEnabled = false
            private set

        // this does not extend delegate because isLagSlayerEnabled should not desync
        private val power = Power(startEnabled = true)

        init {
            power.listenEach(SendPacketEvent) { packet ->
                if (packet !is ServerboundChatCommandPacket) return@listenEach
                if (!packet.command.equals("lagslayer", true)) return@listenEach

                isLagSlayerEnabled = !isLagSlayerEnabled
            }
        }
    }

    val NightVision = requester("/nightvis", DFStateDetectors, trial(
        ReceiveMessageEvent.Chat,
        Unit,
        start = { Sender.sendCommand("nightvis") },
        tests = t@{ (message), _, _ ->
            val enabled = when (message.plainText) {
                "$MAIN_ARROW Enabled night vision." -> true
                "$MAIN_ARROW Disabled night vision." -> false
                else -> return@t null
            }
            instant(enabled)
        }
    ))

    val ResetCompact = Sender(DFStateDetectors) { _: Unit ->
        Sender.sendCommand("resetcompact")
    }

    val Tip = Sender(JoinDFDetector) { _: Unit ->
        Sender.sendCommand("tip")
    }

    val Wand = Sender(DFStateDetectors) { _: Unit ->
        Sender.sendCommand("/wand")
    }
}