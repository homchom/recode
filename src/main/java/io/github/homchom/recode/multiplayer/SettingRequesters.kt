package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.trial.nullaryTrial
import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.toggleRequesterGroup
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.multiplayer.state.DFStateDetectors
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.ui.unstyledString
import io.github.homchom.recode.util.regex.cachedRegex
import io.github.homchom.recode.util.regex.regex

val ChatLocalRequester = requester("/chat local", DFStateDetectors.ChangeMode, nullaryTrial(
    ReceiveChatMessageEvent,
    start = { sendCommand("chat local") },
    tests = { (text), _ ->
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
    start = { time: Long -> sendCommand("time $time") },
    tests = { time, (text), _ ->
        timeRegex(time).matchesUnstyled(text).instantUnitOrNull()
    }
))

val FlightRequesters = toggleRequesterGroup<Unit>("/fly", DFStateDetectors, trial(
    basis = ReceiveChatMessageEvent,
    start = { sendCommand("fly") },
    tests = { input, message, isRequest ->
        val enabled = when (message().unstyledString) {
            "$MAIN_ARROW Flight enabled." -> true
            "$MAIN_ARROW Flight disabled." -> false
            else -> fail()
        }
        if (isRequest && input!!.shouldBeEnabled != enabled) message.invalidate()
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

val LagSlayerRequesters = toggleRequesterGroup<Unit>("/lagslayer", DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    start = { sendCommand("lagslayer") },
    tests = { input, message, isRequest ->
        val enabled = when {
            lsEnabledRegex.matchesUnstyled(message()) -> true
            lsDisabledRegex.matchesUnstyled(message()) -> false
            else -> fail()
        }
        if (isRequest && input!!.shouldBeEnabled != enabled) message.invalidate()
        instant(enabled)
    }
))

val NightVisionRequesters = toggleRequesterGroup<Unit>("/nightvis", DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    start = { sendCommand("nightvis") },
    tests = { input, message, isRequest: Boolean ->
        val enabled = when (message().unstyledString) {
            "$MAIN_ARROW Enabled night vision." -> true
            "$MAIN_ARROW Disabled night vision." -> false
            else -> fail()
        }
        if (isRequest && input!!.shouldBeEnabled != enabled) message.invalidate()
        instant(enabled)
    }
))