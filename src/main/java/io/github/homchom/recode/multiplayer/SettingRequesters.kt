package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.nullaryTrial
import io.github.homchom.recode.event.requester
import io.github.homchom.recode.event.toggleRequesterGroup
import io.github.homchom.recode.event.trial
import io.github.homchom.recode.multiplayer.state.DFStateDetectors
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.ui.unstyledString
import io.github.homchom.recode.util.cachedRegex

val ChatLocalRequester = requester(DFStateDetectors.ChangeMode, nullaryTrial(
    ReceiveChatMessageEvent,
    start = { sendCommand("chat local") },
    tests = { (text), _ ->
        val message = "$MAIN_ARROW_CHAR Chat is now set to Local. You will only see messages from players on " +
                "your plot. Use /chat to change it again."
        text.equalsUnstyled(message).instantUnitOrNull()
    }
))

private val timeRegex = cachedRegex<Long> { time ->
    Regex("""$MAIN_ARROW_CHAR Set your player time to ${time ?: "[0-9+]"}\.""")
}

// TODO: support time keywords through command suggestions (not enum)
val ClientTimeRequester = requester(DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    start = { time: Long -> sendCommand("time $time") },
    tests = { time, (text), _ ->
        timeRegex(time).matchesUnstyled(text).instantUnitOrNull()
    }
))

val FlightRequesters = toggleRequesterGroup<Unit>(DFStateDetectors, trial(
    basis = ReceiveChatMessageEvent,
    start = { sendCommand("fly") },
    tests = { input, message, isRequest ->
        val enabled = when (message().unstyledString) {
            "$MAIN_ARROW_CHAR Flight enabled." -> true
            "$MAIN_ARROW_CHAR Flight disabled." -> false
            else -> fail()
        }
        if (isRequest && input!!.shouldBeEnabled != enabled) message.invalidate()
        instant(enabled)
    }
))

private val lsEnabledRegex =
    Regex("""$LAGSLAYER_PATTERN Now monitoring plot (\d+)\. Type /lagslayer to stop monitoring\.""")
private val lsDisabledRegex =
    Regex("""$LAGSLAYER_PATTERN Stopped monitoring plot (\d+)\.""")

val LagSlayerRequesters = toggleRequesterGroup<Unit>(DFStateDetectors.ChangeMode, trial(
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

val NightVisionRequesters = toggleRequesterGroup<Unit>(DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    start = { sendCommand("nightvis") },
    tests = { input, message, isRequest: Boolean ->
        val enabled = when (message().unstyledString) {
            "$MAIN_ARROW_CHAR Enabled night vision." -> true
            "$MAIN_ARROW_CHAR Disabled night vision." -> false
            else -> fail()
        }
        if (isRequest && input!!.shouldBeEnabled != enabled) message.invalidate()
        instant(enabled)
    }
))