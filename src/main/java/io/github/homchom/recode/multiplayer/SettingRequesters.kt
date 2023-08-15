package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.toggleRequesterGroup
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.multiplayer.state.DFStateDetectors
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.ui.unstyledString
import io.github.homchom.recode.util.regex.cachedRegex
import io.github.homchom.recode.util.regex.regex

val ChatLocalRequester = requester("/chat local", DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    Unit,
    start = { sendCommand("chat local") },
    tests = { (text), _, _ ->
        val message = "$MAIN_ARROW Chat is now set to Local. You will only see messages from players on " +
                "your plot. Use /chat to change it again."
        text.equalsUnstyled(message).instantUnitOrNull()
    }
))

private val timeRegex = cachedRegex<Long> { time ->
    str("$MAIN_ARROW Set your player time to ")
    if (time == null) digit.oneOrMore() else str(time.toString())
    period
}

// TODO: support time keywords through command suggestions (not enum)
val ClientTimeRequester = requester("/time", DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    null as Long?,
    start = { time -> sendCommand("time $time") },
    tests = { context, time, _: Boolean ->
        timeRegex(time).matchesUnstyled(context.value).instantUnitOrNull()
    }
))

val FlightRequesters = toggleRequesterGroup("/fly", DFStateDetectors, trial(
    ReceiveChatMessageEvent,
    Unit,
    start = { sendCommand("fly") },
    tests = { message, _, _ ->
        val enabled = when (message().unstyledString) {
            "$MAIN_ARROW Flight enabled." -> true
            "$MAIN_ARROW Flight disabled." -> false
            else -> fail()
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

val LagSlayerRequesters = toggleRequesterGroup("/lagslayer", DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    Unit,
    start = { sendCommand("lagslayer") },
    tests = { (message), _, _ ->
        val enabled = when {
            lsEnabledRegex.matchesUnstyled(message) -> true
            lsDisabledRegex.matchesUnstyled(message) -> false
            else -> fail()
        }
        instant(enabled)
    }
))

val NightVisionRequesters = toggleRequesterGroup("/nightvis", DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    Unit,
    start = { sendCommand("nightvis") },
    tests = { (message), _, _ ->
        val enabled = when (message.unstyledString) {
            "$MAIN_ARROW Enabled night vision." -> true
            "$MAIN_ARROW Disabled night vision." -> false
            else -> fail()
        }
        instant(enabled)
    }
))