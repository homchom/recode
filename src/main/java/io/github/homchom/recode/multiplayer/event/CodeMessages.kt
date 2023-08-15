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
import io.github.homchom.recode.util.regex.namedGroupValues
import io.github.homchom.recode.util.regex.regex
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object SupportTimeRequester : Requester<Unit, Case<Duration?>> by requester(
    "/support time",
    DFStateDetectors.EndSession,
    trial(
        ReceiveChatMessageEvent,
        Unit,
        start = { sendCommand("support time") },
        tests = tests@{ message, _, _ ->
            if (message.value.equalsUnstyled("Error: You are not in a session.")) {
                return@tests instant(Case.ofNull)
            }

            val regex = regex {
                str("Current session time: ")
                val hours by digit * (1..2)
                val minutes by digit * 2
                val seconds by digit * 2
            }
            val values = regex.matchEntireUnstyled(message())!!.namedGroupValues

            val hours = values["hours"].toIntOrNull()?.hours ?: fail()
            val minutes = values["minutes"].toIntOrNull()?.minutes ?: fail()
            val seconds = values["seconds"].toIntOrNull()?.seconds ?: fail()

            if (hidden) message.invalidate()
            instant(Case(hours + minutes + seconds))
        }
    )
)