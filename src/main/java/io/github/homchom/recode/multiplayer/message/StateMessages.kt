package io.github.homchom.recode.multiplayer.message

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.RIGHT_ARROW
import io.github.homchom.recode.multiplayer.sendCommand
import io.github.homchom.recode.multiplayer.state.*
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.RegexPatternBuilder
import io.github.homchom.recode.util.regex.namedGroupValues
import io.github.homchom.recode.util.regex.regex
import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.RegExp

object StateMessages {
    val parsers get() = arrayOf<MessageParser<*, *>>(Locate, Profile)

    data class Locate(val username: String, val state: LocateState) : ParsedMessage {
        companion object : MessageParser<String, Locate>,
            Requester<String, Locate> by ParsedMessage.requester<String?, Locate>(
                DFStateDetectors.LeaveServer,
                null,
                start = { sendCommand("locate $it") }
            )
        {
            private val locateRegex = regex {
                space * 39; newline
                group {
                    str("You are")
                    or
                    val player by username()
                    str(" is")
                }
                str(" currently ")
                group {
                    str("at spawn")
                    or
                    val mode by anyStr("playing", "building", "coding")
                    str(" on:"); newline

                    bullet()
                    val plotName by any.oneOrMore()
                    str(" [")
                    val plotID by digit.oneOrMore()
                    str("]")

                    group {
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

            override fun match(input: Component): Locate? {
                val values = locateRegex
                    .matchEntireUnstyled(input)
                    ?.namedGroupValues
                    ?: return null
                val player = values["player"].takeUnless(String::isEmpty) ?: mc.player?.username ?: return null
                val node = nodeByName(values["node"])
                val state = if (values["mode"].isEmpty()) {
                    LocateState.AtSpawn(node)
                } else {
                    val mode = PlotMode.ID.entries.singleOrNull { it.descriptor == values["mode"] } ?: return null
                    val plotName = values["plotName"]
                    val plotID = values["plotID"].toUIntOrNull() ?: return null
                    val owner = values["owner"]
                    val status = values["status"].takeUnless(String::isEmpty)

                    LocateState.OnPlot(node, Plot(plotName, owner, plotID), mode, status)
                }
                return Locate(player, state)
            }
        }
    }

    data class Profile(val username: String, val ranks: List<Rank>) : ParsedMessage {
        companion object : MessageParser<String, Profile>,
            Requester<String, Profile> by ParsedMessage.requester<String?, Profile>(
                DFStateDetectors.LeaveServer,
                null,
                start = { sendCommand("profile $it") }
            )
        {
            private val profileRegex = regex {
                space * 39; newline
                str("Profile of ")
                val player by username()
                space
                group {
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

            override fun match(input: Component): Profile? {
                val values = profileRegex
                    .matchEntireUnstyled(input)
                    ?.namedGroupValues
                    ?: return null

                val player = values["player"]
                if (player.isEmpty()) return null

                val rankMap = DonorRank.entries.associateBy { it.displayName }
                val rankString = values["ranks"]
                val ranks = if (rankString.isEmpty()) emptyList() else rankString
                    .substring(1, rankString.length - 1)
                    .split("][")
                    .mapNotNull(rankMap::get)

                return Profile(player, ranks)
            }
        }
    }

    @RegExp
    private fun RegexPatternBuilder.bullet() {
        newline; str(RIGHT_ARROW); space
    }
}