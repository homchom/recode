package io.github.homchom.recode.multiplayer.event

import io.github.homchom.recode.event.*
import io.github.homchom.recode.event.trial.requester
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.*
import io.github.homchom.recode.multiplayer.state.*
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.util.cachedRegex
import io.github.homchom.recode.util.namedGroupValues
import org.intellij.lang.annotations.Language
import org.intellij.lang.annotations.RegExp

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
                    val mode = PlotMode.ID.entries.singleOrNull { it.descriptor == values["mode"] } ?: fail()
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

// TODO: is there a clean way to make *any* requester invalidatable if its basis is validated? (if so, unneeded)
data class UserStateRequest(val username: String, val hideMessage: Boolean = false)

@RegExp
private fun usernamePattern(username: String?) =
    """(?<player>${username?.let(Regex::escape) ?: USERNAME_PATTERN})"""

@RegExp
private fun bullet(@RegExp pattern: String) = """\n$RIGHT_ARROW_CHAR $pattern"""

@RegExp
private fun optionalBullet(@RegExp pattern: String) = """(?:${bullet(pattern)})?"""