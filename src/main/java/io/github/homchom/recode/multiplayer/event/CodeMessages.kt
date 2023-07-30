package io.github.homchom.recode.multiplayer.event

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.sendCommand
import io.github.homchom.recode.multiplayer.state.DFStateDetectors
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.util.Case
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object SupportTimeRequester : Requester<Boolean, Case<Duration?>> by requester(
    "/support time",
    DFStateDetectors.EndSession,
    trial(
        ReceiveChatMessageEvent,
        start = { sendCommand("support time") },
        tests = tests@{ hideMessage, message, _ ->
            if (message.value.equalsUnstyled("Error: You are not in a session.")) {
                return@tests instant(Case.ofNull)
            }

            val regex = Regex("""Current session time: (\d?\d):(\d\d):(\d\d)""")
            val match = regex.matchEntireUnstyled(message.value)!!

            val hours = match.groupValues[1].toIntOrNull()?.hours ?: fail()
            val minutes = match.groupValues[2].toIntOrNull()?.minutes ?: fail()
            val seconds = match.groupValues[3].toIntOrNull()?.seconds ?: fail()

            if (hideMessage == true) message.invalidate()
            instant(Case(hours + minutes + seconds))
        }
    )
)