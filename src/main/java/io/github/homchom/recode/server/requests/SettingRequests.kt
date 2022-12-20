package io.github.homchom.recode.server.requests

import io.github.homchom.recode.mc
import io.github.homchom.recode.server.*
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.defaultedRegex
import io.github.homchom.recode.util.unitOrNull
import net.minecraft.world.effect.MobEffects

val ChatLocal by defineNullaryRequest(
    ReceiveChatMessageEvent,
    executor = { sendCommand("chat local") },
    matcher = { text -> text.equalsUnstyled("Your chat is now set to LOCAL").unitOrNull() }
)

private val timeRegex = defaultedRegex<Long> { time ->
    Regex("""$GREEN_ARROW_CHAR Set your player time to ${time ?: "[0-9+]"}.""")
}

// TODO: support time keywords through command suggestions, not enum
val ClientTime by defineRequest(
    ReceiveChatMessageEvent,
    executor = { time: Long -> sendCommand("time $time") },
    matcher = { text, time ->
        timeRegex(time).matchesUnstyled(text).unitOrNull()
    }
)

private val nvEnabledRegex = Regex("$GREEN_ARROW_CHAR Enabled night vision.")
private val nvDisabledRegex = Regex("$GREEN_ARROW_CHAR Disabled night vision")

val NightVision = toggleRequestHolder(
    ReceiveChatMessageEvent,
    executor = { _: Unit -> sendCommand("nightvis") },
    enabledPredicate = { mc.player!!.hasEffect(MobEffects.NIGHT_VISION) },
    enabledMatcher = { text, _ -> nvEnabledRegex.matchesUnstyled(text).unitOrNull() },
    disabledMatcher = { text, _ -> nvDisabledRegex.matchesUnstyled(text).unitOrNull() }
)

val LagSlayer = toggleRequestHolder(
    ReceiveChatMessageEvent,
    executor = { _: UInt -> sendCommand("lagslayer") },
    enabledPredicate = { mc.player!!.hasEffect(MobEffects.NIGHT_VISION) },
    enabledMatcher = { text, _ -> nvEnabledRegex.matchesUnstyled(text).unitOrNull() },
    disabledMatcher = { text, _ -> nvDisabledRegex.matchesUnstyled(text).unitOrNull() }
)