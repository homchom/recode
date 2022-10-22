package io.github.homchom.recode.server

import io.github.homchom.recode.mc
import io.github.homchom.recode.ui.literalText
import io.github.homchom.recode.ui.stringWithoutColor
import io.github.homchom.recode.util.Matcher
import io.github.homchom.recode.util.MatcherList
import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language

sealed interface Message {
    class Locate(val username: String, val state: LocateState) : Message
    @JvmInline value class PlayerNotFound(override val text: Component) : ErrorMessage
    object Chat : Message
}

sealed interface ErrorMessage : Message {
    val text: Component

    object PlayerNotFound : ErrorMessage {
        override val text = literalText("Error: Could not find that player.")
    }
}

private val matchers = MatcherList<Component, Message> { Message.Chat }

object MessageMatcher : Matcher<Component, Message> by matchers {
    private val LOCATE_REGEX = run {
        @Language("regexp") val player = """(?:(You) are|(\w{3,16}) is) currently"""
        @Language("regexp") val mode = """(playing|building|coding) on:\n"""
        @Language("regexp") val plot = """\n→ (.{1,128}) \[(\d+)]"""
        @Language("regexp") val owner = """\n→ Owner: (\w{3,16})"""
        @Language("regexp") val status = """(?:\n→ Status: (.+))?"""
        @Language("regexp") val server = """\n→ Server: ([A-Za-z\d ]+)"""
        Regex(""" {39}\n$player (?:(at spawn)\n|$mode$plot$owner$status)$server\n {39}""")
    }

    val LOCATE = matchers.add { text ->
        val values = LOCATE_REGEX.matchEntire(text.stringWithoutColor)?.groupValues ?: fail()
        val username = values[1].let { if (it == "You") mc.player!!.scoreboardName else it }
        val node = nodeByName(values[8]) // the last capturing group
        val state = if (values[2] == "at spawn") {
            LocateState.AtSpawn(username, node)
        } else {
            val mode = plotModeByDescriptorOrNull(values[3]) ?: fail()
            val plotName = values[4]
            val plotID = values[5].toUIntOrNull() ?: fail()
            val owner = values[6]
            val status = values[7].takeUnless { it == "" }
            LocateState.OnPlot(
                username,
                node,
                Plot(plotName, owner, plotID),
                mode,
                status
            )
        }
        Message.Locate(username, state)
    }
}