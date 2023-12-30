package io.github.homchom.recode.hypercube.message

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.hypercube.state.DFStateDetectors
import io.github.homchom.recode.multiplayer.sendCommand
import io.github.homchom.recode.ui.text.equalsPlain
import io.github.homchom.recode.ui.text.matchEntirePlain
import io.github.homchom.recode.util.regex.groupValue
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.text.Component
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
            private val regex = regex {
                str("Current session time: ")
                val hours by digit * (1..2)
                val minutes by digit * 2
                val seconds by digit * 2
            }

            override fun match(input: Component): SupportTime? {
                if (input.equalsPlain("Error: You are not in a session.")) {
                    return SupportTime(null)
                }

                val match = regex.matchEntirePlain(input) ?: return null

                val hours = match.groupValue("hours").toIntOrNull()?.hours ?: return null
                val minutes = match.groupValue("minutes").toIntOrNull()?.minutes ?: return null
                val seconds = match.groupValue("seconds").toIntOrNull()?.seconds ?: return null

                return SupportTime(hours + minutes + seconds)
            }
        }
    }
}