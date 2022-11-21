package io.github.homchom.recode.server

import io.github.homchom.recode.mc
import io.github.homchom.recode.server.state.*
import io.github.homchom.recode.ui.stringWithoutColor
import io.github.homchom.recode.util.Matcher
import io.github.homchom.recode.util.MatcherList
import io.github.homchom.recode.util.NullableScope
import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import org.intellij.lang.annotations.RegExp

sealed interface Message {
    data class EnterPlotMode(val mode: PlotMode) : Message
    data class Locate(val username: String, val state: LocateState) : Message
    data class Chat(val text: Component) : Message
}

sealed interface ErrorMessage : Message {
    object PlayerNotFound : ErrorMessage
}

private val matchers = MatcherList<Component, Message>(Message::Chat)

// TODO: split into multiple MatcherLists

object MessageMatcher : Matcher<Component, Message> by matchers {
    val Locate = addRegexMatcher(
        pattern = run {
            @Language("regexp") val player = """(?:(You) are|($USERNAME_PATTERN) is) currently"""
            @Language("regexp") val mode = """(playing|building|coding) on:\n"""
            @Language("regexp") val plot = """\n→ ($PLOT_NAME_PATTERN) \[(\d+)]"""
            @Language("regexp") val owner = """\n→ Owner: ($USERNAME_PATTERN)"""
            @Language("regexp") val status = """(?:\n→ Status: (.+))?"""
            @Language("regexp") val server = """\n→ Server: ([A-Za-z\d ]+)"""
            """ {39}\n$player (?:(at spawn)\n|$mode$plot$owner$status)$server\n {39}"""
        }
    ) { match ->
        val values = match.groupValues
        val username = values[1].let { if (it == "You") mc.player!!.scoreboardName else it }
        val node = nodeByName(values[8]) // the last capturing group
        val state = if (values[2] == "at spawn") {
            LocateState.AtSpawn(node)
        } else {
            val mode = plotModeByDescriptorOrNull(values[3]) ?: fail()
            val plotName = values[4]
            val plotID = values[5].toUIntOrNull() ?: fail()
            val owner = values[6]
            val status = values[7].takeUnless { it == "" }
            LocateState.OnPlot(node, Plot(plotName, owner, plotID), mode, status)
        }
        Message.Locate(username, state)
    }

    // Error Messages

    val PlayerNotFound = addLiteralMatcher("Error: Could not find that player.", ErrorMessage.PlayerNotFound)

    private fun addRegexMatcher(
        @RegExp pattern: String,
        matcher: NullableScope.(MatchResult) -> Message
    ): Matcher<Component, Message> {
        val regex = Regex(pattern)
        return matchers.add { text ->
            matcher(regex.matchEntire(text.stringWithoutColor) ?: fail())
        }
    }

    private fun addLiteralMatcher(literal: String, message: Message) = matchers.add { text ->
        if (text.stringWithoutColor == literal) message else null
    }
}