package io.github.homchom.recode.server

import io.github.homchom.recode.event.nullaryTrial
import io.github.homchom.recode.event.requester
import io.github.homchom.recode.event.toggleRequesterGroup
import io.github.homchom.recode.event.trial
import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.cachedRegexBuilder
import io.github.homchom.recode.util.unitOrNull
import net.minecraft.world.effect.MobEffects

val ChatLocalRequester = requester(nullaryTrial(
    ReceiveChatMessageEvent,
    start = { sendCommand("chat local") },
    tests = { text, _ -> text.equalsUnstyled("Your chat is now set to LOCAL").unitOrNull() }
))

private val timeRegex = cachedRegexBuilder<Long> { time ->
    Regex("""$GREEN_ARROW_CHAR Set your player time to ${time ?: "[0-9+]"}.""")
}

// TODO: support time keywords through command suggestions, not enum
val ClientTimeRequester = requester(trial(
    ReceiveChatMessageEvent,
    start = { time: Long -> sendCommand("time $time") },
    tests = { time, text, _ ->
        timeRegex(time).matchesUnstyled(text).unitOrNull()
    }
))

private val nvEnabledRegex = Regex("$GREEN_ARROW_CHAR Enabled night vision.")
private val nvDisabledRegex = Regex("$GREEN_ARROW_CHAR Disabled night vision")

val NightVisionRequesters = toggleRequesterGroup(
    ReceiveChatMessageEvent,
    start = { _: Unit? -> sendCommand("nightvis") },
    enabledPredicate = { mc.player!!.hasEffect(MobEffects.NIGHT_VISION) },
    enabledTests = { _, text, _ -> nvEnabledRegex.matchesUnstyled(text).unitOrNull() },
    disabledTests = { _, text, _ -> nvDisabledRegex.matchesUnstyled(text).unitOrNull() }
)

val LagSlayerRequesters = toggleRequesterGroup(
    ReceiveChatMessageEvent,
    start = { _: Unit? -> sendCommand("lagslayer") },
    enabledPredicate = { mc.player!!.hasEffect(MobEffects.NIGHT_VISION) },
    enabledTests = { _, text, _ -> nvEnabledRegex.matchesUnstyled(text).unitOrNull() },
    disabledTests = { _, text, _ -> nvDisabledRegex.matchesUnstyled(text).unitOrNull() }
)