package io.github.homchom.recode.server.message

import io.github.homchom.recode.event.*
import io.github.homchom.recode.mc
import io.github.homchom.recode.server.*
import io.github.homchom.recode.server.message.LocateMessage.Companion.regex
import io.github.homchom.recode.server.message.TipMessage.Companion.mainRegex
import io.github.homchom.recode.server.message.TipMessage.Companion.timeRegex
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.cachedRegexBuilder
import io.github.homchom.recode.util.namedGroupValues
import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import org.intellij.lang.annotations.RegExp

data class LocateMessage(val username: String, val state: LocateState) {
    companion object : Requester<String, LocateMessage> by requester(trial(
        ReceiveChatMessageEvent,
        start = { username -> sendCommand("locate $username") },
        tests = { username, text: Component, _ ->
            val values = regex(username).matchEntireUnstyled(text)?.namedGroupValues ?: fail()
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
            LocateMessage(player, state)
        }
    )) {
        private val regex = cachedRegexBuilder<String> { username ->
            @RegExp fun bullet(name: String, @RegExp pattern: String) = """\n$RIGHT_ARROW_CHAR $name: $pattern"""

            val player = (if (username == null) USERNAME_PATTERN else Regex.escape(username))
                .let { """(?:You are|(?<player>$it) is) currently""" }
            @Language("regexp") val mode = """(?<mode>playing|building|coding) on:\n"""
            @Language("regexp") val plot =
                """\n$RIGHT_ARROW_CHAR (?<plotName>$PLOT_NAME_PATTERN) \[(?<plotID>\d+)]"""
            @Language("regexp") val owner =
                bullet("Owner", """(?<owner>$USERNAME_PATTERN) (?:\[Whitelisted])?""")
            @Language("regexp") val status = """(?:${bullet("Status", """(?<status>.+)""")})?"""
            @Language("regexp") val server = bullet("Server", """(?<node>[A-Za-z\d ]+)""")

            Regex(""" {39}\n$player (?:at spawn|$mode$plot$owner$status)$server\n {39}""")
        }
    }
}

data class TipMessage(val player: String) {
    companion object : Detector<Unit, TipMessage> by detector(nullaryTrial(
        ReceiveChatMessageEvent,
        tests = { message ->
            val player = mainRegex.matchEntireUnstyled(message)?.groupValues?.get(1) ?: fail()
            +testBooleanOn(ReceiveChatMessageEvent, 2u) { timeRegex.matchesUnstyled(it) }
            TipMessage(player)
        }
    )) {
        private val mainRegex =
            Regex("""$BOOSTER_ARROW_SYMBOL ($USERNAME_PATTERN) is using a \d+x booster.""")
        private val timeRegex =
            Regex("""$BOOSTER_ARROW_SYMBOL The booster wears off in \d+ (?:day|hour|minute|second)s?.""")
    }
}