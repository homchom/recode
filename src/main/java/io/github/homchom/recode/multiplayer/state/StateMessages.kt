package io.github.homchom.recode.multiplayer.state

import io.github.homchom.recode.event.*
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.ui.equalsUnstyled
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.cachedRegex
import io.github.homchom.recode.util.namedGroupValues
import org.intellij.lang.annotations.Language
import org.intellij.lang.annotations.RegExp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

data class LocateMessage(val username: String, val state: LocateState) {
    companion object : Requester<UserStateRequest, LocateMessage> by requester(
        "/locate",
        DFStateDetectors.LeaveServer,
        trial(
            ReceiveChatMessageEvent,
            start = { request -> sendCommand("locate ${request.username}") },
            tests = { request, context, _ ->
                val message = context.value
                val values = LocateMessage.regex(request?.username)
                    .matchEntireUnstyled(message)!!
                    .namedGroupValues
                val player = values["player"].let { if (it == "You") mc.player!!.username else it }
                val node = nodeByName(values["node"])
                val state = if (values["mode"].isEmpty()) {
                    LocateState.AtSpawn(node)
                } else {
                    val mode = plotModeByDescriptorOrNull(values["mode"]) ?: fail()
                    val plotName = values["plotName"]
                    val plotID = values["plotID"].toUIntOrNull() ?: fail()
                    val owner = values["owner"]
                    val status = values["status"].takeUnless(String::isEmpty)
                    LocateState.OnPlot(node, Plot(plotName, owner, plotID), mode, status)
                }
                if (request?.hideMessage == true) context.invalidate()
                instant(LocateMessage(player, state))
            }
        )
    ) {
        private val regex = cachedRegex<String> { username ->
            @Language("regexp") val player = """(?:You are|${usernamePattern(username)} is) currently"""
            @Language("regexp") val mode = """(?<mode>playing|building|coding) on:\n"""
            @Language("regexp") val plot =
                bullet("""(?<plotName>$PLOT_NAME_PATTERN) \[(?<plotID>\d+)]""")
            @Language("regexp") val status = optionalBullet("""(?<status>.+)""")
            @Language("regexp") val owner =
                bullet("""Owner: (?<owner>$USERNAME_PATTERN) (?:\[Whitelisted])?""")
            @Language("regexp") val server = bullet("""Server: (?<node>[A-Za-z\d ]+)""")

            Regex(""" {39}\n$player (?:at spawn|$mode$plot$status$owner)$server\n {39}""")
        }
    }
}

data class ProfileMessage(val username: String, val ranks: List<Rank>) {
    companion object : Requester<UserStateRequest, ProfileMessage> by requester(
        "/profile",
        DFStateDetectors.LeaveServer,
        trial(
            ReceiveChatMessageEvent,
            start = { request -> sendCommand("profile ${request.username}") },
            tests = { request, context, _ ->
                val message = context.value
                val values = ProfileMessage.regex(request?.username)
                    .matchEntireUnstyled(message)!!
                    .namedGroupValues
                val player = values["player"]

                val rankMap = DonorRank.entries.associateBy { it.displayName }
                val rankString = values["ranks"]
                val ranks = if (rankString.isEmpty()) emptyList() else rankString
                    .substring(1, rankString.length - 1)
                    .split("][")
                    .mapNotNull { rankMap[it] }

                if (request?.hideMessage == true) context.invalidate()
                instant(ProfileMessage(player, ranks))
            }
        )
    ) {
        private val regex = cachedRegex<String> { username ->
            @Language("regexp") val player = """Profile of ${usernamePattern(username)} (?:\(.+?\))?\n"""
            @Language("regexp") val ranks = bullet("""Ranks: (?<ranks>.*?)""")

            Regex(""" {39}\n$player$ranks\n(?s).+ {39}""")
        }
    }
}

data class TipMessage(val player: String, val canTip: Boolean) {
    companion object : Detector<Unit, TipMessage> by detector("/tip", nullaryTrial(
        ReceiveChatMessageEvent,
        tests = { (message) ->
            val player = TipMessage.mainRegex.matchEntireUnstyled(message)!!.groupValues[1]
            val subsequent = ReceiveChatMessageEvent.add()

            suspending {
                val (first) = subsequent.receive()
                val canTip = TipMessage.commandRegex.matchesUnstyled(first)

                if (!TipMessage.timeRegex.matchesUnstyled(first)) {
                    +testBoolean(subsequent) { (second) ->
                        TipMessage.timeRegex.matchesUnstyled(second)
                    }
                }

                TipMessage(player, canTip)
            }
        }
    )) {
        private val mainRegex =
            Regex("""$BOOSTER_ARROW_PATTERN ($USERNAME_PATTERN) is using a \d+x booster\.""")
        private val commandRegex =
            Regex("""$BOOSTER_ARROW_PATTERN Use /tip to show your appreciation """ +
                    """and receive a $TOKEN_NOTCH_CHAR token notch!""")
        private val timeRegex =
            Regex("""$BOOSTER_ARROW_PATTERN The booster wears off in \d+ (?:day|hour|minute|second)s?\.""")
    }
}

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

// TODO: is there a clean way to make *any* requester invalidatable if its basis is validated? (if so, unneeded)
data class UserStateRequest(val username: String, val hideMessage: Boolean = false)

@RegExp
private fun usernamePattern(username: String?) =
    """(?<player>${username?.let(Regex::escape) ?: USERNAME_PATTERN})"""

@RegExp
private fun bullet(@RegExp pattern: String) = """\n$RIGHT_ARROW_CHAR $pattern"""

@RegExp
private fun optionalBullet(@RegExp pattern: String) = """(?:${bullet(pattern)})?"""