package io.github.homchom.recode.hypercube.message

import io.github.homchom.recode.event.Requester
import io.github.homchom.recode.event.merge
import io.github.homchom.recode.hypercube.RIGHT_ARROW
import io.github.homchom.recode.hypercube.state.*
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.DisconnectFromServerEvent
import io.github.homchom.recode.multiplayer.JoinServerEvent
import io.github.homchom.recode.multiplayer.Sender
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.text.matchEntirePlain
import io.github.homchom.recode.util.regex.RegexModifier
import io.github.homchom.recode.util.regex.RegexPatternBuilder
import io.github.homchom.recode.util.regex.groupValue
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.text.Component
import org.intellij.lang.annotations.RegExp

object StateMessages {
    val parsers get() = arrayOf<MessageParser<*, *>>(Locate, Profile)

    private val lifecycle = merge(JoinServerEvent, DisconnectFromServerEvent)

    data class Locate(val username: String, val state: LocateState) : ParsedMessage {
        companion object : MessageParser<String, Locate>,
            Requester<String, Locate> by ParsedMessage.requester<String?, Locate>(
                lifecycle,
                null,
                start = { Sender.sendCommand("locate $it") }
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
                val match = locateRegex.matchEntirePlain(input) ?: return null
                val player = match.groups["player"]?.value
                    ?: mc.player?.username
                    ?: return null
                val node = nodeByName(match.groupValue("node"))
                val modeString = match.groups["mode"]?.value
                val state = if (modeString == null) {
                    LocateState.AtSpawn(node)
                } else {
                    val mode = PlotMode.ID.entries.singleOrNull { it.descriptor == modeString }
                        ?: return null
                    val plotName = match.groupValue("plotName")
                    val plotID = match.groupValue("plotID").toUIntOrNull() ?: return null
                    val owner = match.groupValue("owner")
                    val status = match.groups["status"]?.value

                    LocateState.OnPlot(node, Plot(plotName, owner, plotID), mode, status)
                }
                return Locate(player, state)
            }
        }
    }

    data class Profile(val username: String, val ranks: List<Rank>) : ParsedMessage {
        companion object : MessageParser<String, Profile>,
            Requester<String, Profile> by ParsedMessage.requester<String?, Profile>(
                lifecycle,
                null,
                start = { Sender.sendCommand("profile $it") }
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
                val match = profileRegex.matchEntirePlain(input) ?: return null

                val player = match.groups["player"]?.value ?: return null
                val rankMap = DonorRank.entries.associateBy { it.displayName }
                val rankString = match.groups["ranks"]?.value
                val ranks = rankString?.substring(1, rankString.length - 1)
                    ?.split("][")
                    ?.mapNotNull(rankMap::get)
                    ?: emptyList()

                return Profile(player, ranks)
            }
        }
    }

    @RegExp
    private fun RegexPatternBuilder.bullet() {
        newline; str(RIGHT_ARROW); space
    }
}