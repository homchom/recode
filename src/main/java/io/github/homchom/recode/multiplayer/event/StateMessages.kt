package io.github.homchom.recode.multiplayer.event

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.RIGHT_ARROW
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.sendCommand
import io.github.homchom.recode.multiplayer.state.*
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.RegexPatternBuilder
import io.github.homchom.recode.util.regex.cachedRegex
import io.github.homchom.recode.util.regex.namedGroupValues
import org.intellij.lang.annotations.RegExp

data class LocateMessage(val username: String, val state: LocateState) {
    companion object : Requester<String, LocateMessage> by requester(
        "/locate",
        DFStateDetectors.LeaveServer,
        trial(
            ReceiveChatMessageEvent,
            null,
            start = { username ->
                if (username.isEmpty()) {
                    sendCommand("locate")
                } else {
                    sendCommand("locate $username")
                }
            },
            tests = { message, username, _ ->
                val values = LocateMessage.locateRegex(username)
                    .matchEntireUnstyled(message())!!
                    .namedGroupValues
                val player = values["player"].takeUnless(String::isEmpty) ?: mc.player!!.username
                val node = nodeByName(values["node"])
                val state = if (values["mode"].isEmpty()) {
                    LocateState.AtSpawn(node)
                } else {
                    val mode = PlotMode.ID.entries.singleOrNull { it.descriptor == values["mode"] } ?: fail()
                    val plotName = values["plotName"]
                    val plotID = values["plotID"].toUIntOrNull() ?: fail()
                    val owner = values["owner"]
                    val status = values["status"].takeUnless(String::isEmpty)

                    LocateState.OnPlot(node, Plot(plotName, owner, plotID), mode, status)
                }
                if (hidden) message.invalidate()
                instant(LocateMessage(player, state))
            }
        )
    ) {
        private val locateRegex = cachedRegex<String> { username ->
            space * 39; newline
            all {
                str("You are")
                or
                val player by username(username)
                str(" is")
            }
            str(" currently ")
            all {
                str("at spawn")
                or
                val mode by anyStr("playing", "building", "coding")
                str(" on:"); newline

                bullet()
                val plotName by any.oneOrMore()
                str(" [")
                val plotID by digit.oneOrMore()
                str("]")

                all {
                    bullet()
                    val status by any.oneOrMore().possessive()
                }.optional().lazy()

                bullet()
                str("Owner: ")
                val owner by username()
                space
                str("[Whitelisted]").optional()
            }

            bullet()
            str("Server: ")
            val node by any("\\w ").oneOrMore()

            newline; space * 39
        }
    }
}

data class ProfileMessage(val username: String, val ranks: List<Rank>) {
    companion object : Requester<String, ProfileMessage> by requester(
        "/profile",
        DFStateDetectors.LeaveServer,
        trial(
            ReceiveChatMessageEvent,
            "",
            start = { username ->
                if (username.isEmpty()) {
                    sendCommand("profile")
                } else {
                    sendCommand("profile $username")
                }
            },
            tests = { message, username, _ ->
                val values = ProfileMessage.profileRegex(username)
                    .matchEntireUnstyled(message())!!
                    .namedGroupValues
                val player = values["player"]

                val rankMap = DonorRank.entries.associateBy { it.displayName }
                val rankString = values["ranks"]
                val ranks = if (rankString.isEmpty()) emptyList() else rankString
                    .substring(1, rankString.length - 1)
                    .split("][")
                    .mapNotNull { rankMap[it] }

                if (hidden) message.invalidate()
                instant(ProfileMessage(player, ranks))
            }
        )
    ) {
        private val profileRegex = cachedRegex<String> { username ->
            space * 39; newline
            str("Profile of ")
            val player by username(username)
            space
            all {
                str("(")
                any.oneOrMore()
                str(")")
            }.optional()
            newline

            bullet()
            str("Ranks: ")
            val ranks by any.zeroOrMore().possessive()

            newline
            modify(RegexModifier.MatchLineBreaksInAny)
            any.atLeast(22) // faster fail: remaining text will be at least 22 chars
            space * 39
        }
    }
}

@RegExp
private fun RegexPatternBuilder.bullet() {
    newline; str(RIGHT_ARROW); space
}