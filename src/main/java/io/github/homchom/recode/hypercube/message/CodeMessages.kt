package io.github.homchom.recode.hypercube.message

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.hypercube.state.DFStateDetectors
import io.github.homchom.recode.multiplayer.sendCommand
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.util.regex.namedGroupValues
import io.github.homchom.recode.util.regex.regex
import net.minecraft.network.chat.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object CodeMessages {
    val parsers get() = arrayOf(SupportTime)

    data class SupportTime(val duration: Duration?) : ParsedMessage {
        companion object : MessageParser<Unit, SupportTime>,
            Requester<Unit, SupportTime> by ParsedMessage.requester<Unit, SupportTime>(
                DFStateDetectors.EndSession,
                Unit,
                start = { sendCommand("support time") }
            )
        {
            override fun match(input: Component): SupportTime? {
                if (input.equalsUnstyled("Error: You are not in a session.")) {
                    return SupportTime(null)
                }

                val regex = regex {
                    str("Current session time: ")
                    val hours by digit * (1..2)
                    val minutes by digit * 2
                    val seconds by digit * 2
                }
                val values = regex.matchEntireUnstyled(input)?.namedGroupValues ?: return null

                val hours = values["hours"].toIntOrNull()?.hours ?: return null
                val minutes = values["minutes"].toIntOrNull()?.minutes ?: return null
                val seconds = values["seconds"].toIntOrNull()?.seconds ?: return null

                return SupportTime(hours + minutes + seconds)
            }
        }
    }
}