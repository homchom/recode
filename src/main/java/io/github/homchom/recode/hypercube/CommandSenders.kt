package io.github.homchom.recode.hypercube

import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.toggleRequesterGroup
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.hypercube.state.DFStateDetectors
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.Sender
import io.github.homchom.recode.ui.text.equalsPlain
import io.github.homchom.recode.ui.text.matchesPlain
import io.github.homchom.recode.ui.text.plainText
import io.github.homchom.recode.util.regex.dynamicRegex
import io.github.homchom.recode.util.regex.regex

// TODO: replace more requesters with senders and update documentation
object CommandSenders {
    val ChatLocal = requester("/chat local", DFStateDetectors, trial(
        ReceiveChatMessageEvent,
        Unit,
        start = { Sender.sendCommand("chat local") },
        tests = { (text), _, _ ->
            val message = "$MAIN_ARROW Chat is now set to Local. You will only see messages from players on " +
                    "your plot. Use /chat to change it again."
            text.equalsPlain(message).instantUnitOrNull()
        }
    ))

    private val timeRegex = dynamicRegex { time: Long? ->
        str("$MAIN_ARROW Set your player time to ")
        if (time == null) digit.oneOrMore() else str(time.toString())
        period
    }

    // TODO: support time keywords through command suggestions (not enum)
    val ClientTime = requester("/time", DFStateDetectors, trial(
        ReceiveChatMessageEvent,
        null as Long?,
        start = { time -> Sender.sendCommand("time $time") },
        tests = { context, time, _: Boolean ->
            timeRegex(time).matchesPlain(context.value).instantUnitOrNull()
        }
    ))

    val Flight = toggleRequesterGroup("/fly", DFStateDetectors, trial(
        ReceiveChatMessageEvent,
        Unit,
        start = { Sender.sendCommand("fly") },
        tests = t@{ message, _, _ ->
            val enabled = when (message().plainText) {
                "$MAIN_ARROW Flight enabled." -> true
                "$MAIN_ARROW Flight disabled." -> false
                else -> return@t null
            }
            instant(enabled)
        }
    ))

    private val lsEnabledRegex = regex {
        str("$LAGSLAYER_PREFIX Now monitoring plot ")
        val plot by digit.oneOrMore()
        str(". Type /lagslayer to stop monitoring.")
    }
    private val lsDisabledRegex = regex {
        str("$LAGSLAYER_PREFIX Stopped monitoring plot ")
        val plot by digit.oneOrMore()
        period
    }

    val LagSlayer = toggleRequesterGroup("/lagslayer", DFStateDetectors, trial(
        ReceiveChatMessageEvent,
        Unit,
        start = { Sender.sendCommand("lagslayer") },
        tests = t@{ (message), _, _ ->
            val enabled = when {
                lsEnabledRegex.matchesPlain(message) -> true
                lsDisabledRegex.matchesPlain(message) -> false
                else -> return@t null
            }
            instant(enabled)
        }
    ))

    val NightVision = toggleRequesterGroup("/nightvis", DFStateDetectors, trial(
        ReceiveChatMessageEvent,
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