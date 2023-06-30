package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.nullaryToggleRequesterGroup
import io.github.homchom.recode.event.nullaryTrial
import io.github.homchom.recode.event.requester
import io.github.homchom.recode.event.trial
import io.github.homchom.recode.mc
import io.github.homchom.recode.mod.features.LagslayerHUD
import io.github.homchom.recode.multiplayer.state.DFStateDetectors
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.cachedRegexBuilder
import net.minecraft.world.effect.MobEffects

val ChatLocalRequester = requester(DFStateDetectors.ChangeMode, nullaryTrial(
    ReceiveChatMessageEvent,
    start = { sendCommand("chat local") },
    tests = { (text), _ ->
        val message = "$MAIN_ARROW_CHAR Chat is now set to Local. You will only see messages from players on " +
                "your plot. Use /chat to change it again."
        text.equalsUnstyled(message).instantUnitOrNull()
    }
))

private val timeRegex = cachedRegexBuilder<Long> { time ->
    Regex("""$MAIN_ARROW_CHAR Set your player time to ${time ?: "[0-9+]"}\.""")
}

// TODO: support time keywords through command suggestions, not enum
val ClientTimeRequester = requester(DFStateDetectors.ChangeMode, trial(
    ReceiveChatMessageEvent,
    start = { time: Long -> sendCommand("time $time") },
    tests = { time, (text), _ ->
        timeRegex(time).matchesUnstyled(text).instantUnitOrNull()
    }
))

// TODO: support time keywords through command suggestions, not enum
val FlightRequesters = nullaryToggleRequesterGroup(
    DFStateDetectors,
    ReceiveChatMessageEvent,
    start = { sendCommand("fly") },
    enabledPredicate = { mc.player!!.isFlightEnabled },
    enabledTests = { (text), _ ->
        text.equalsUnstyled("$MAIN_ARROW_CHAR Flight enabled.").instantUnitOrNull()
    },
    disabledTests = { (text), _ ->
        text.equalsUnstyled("$MAIN_ARROW_CHAR Flight disabled.").instantUnitOrNull()
    }
)

private val lsEnabledRegex =
    Regex("""$LAGSLAYER_PATTERN Now monitoring plot (\d+)\. Type /lagslayer to stop monitoring\.""")
private val lsDisabledRegex =
    Regex("""$LAGSLAYER_PATTERN Stopped monitoring plot (\d+)\.""")

// TODO: improve enabledPredicate once arbitrary requesters are able to be invalidated
val LagSlayerRequesters = nullaryToggleRequesterGroup(
    DFStateDetectors.ChangeMode,
    ReceiveChatMessageEvent,
    start = { sendCommand("lagslayer") },
    enabledPredicate = { LagslayerHUD.lagSlayerEnabled },
    enabledTests = { (text), _ -> lsEnabledRegex.matchesUnstyled(text).instantUnitOrNull() },
    disabledTests = { (text), _ -> lsDisabledRegex.matchesUnstyled(text).instantUnitOrNull() }
)

val NightVisionRequesters = nullaryToggleRequesterGroup(
    DFStateDetectors.ChangeMode,
    ReceiveChatMessageEvent,
    start = { sendCommand("nightvis") },
    enabledPredicate = { mc.player!!.hasEffect(MobEffects.NIGHT_VISION) },
    enabledTests = { (text), _ ->
        text.equalsUnstyled("$MAIN_ARROW_CHAR Enabled night vision.").instantUnitOrNull()
    },
    disabledTests = { (text), _ ->
        text.equalsUnstyled("$MAIN_ARROW_CHAR Disabled night vision.").instantUnitOrNull()
    }
)