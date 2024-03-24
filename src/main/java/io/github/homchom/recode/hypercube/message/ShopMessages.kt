package io.github.homchom.recode.hypercube.message

import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.hypercube.BOOSTER_ARROW
import io.github.homchom.recode.hypercube.TOKEN_NOTCH_CHAR
import io.github.homchom.recode.multiplayer.ReceiveMessageEvent
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.text.matchEntirePlain
import io.github.homchom.recode.ui.text.matchesPlain
import io.github.homchom.recode.util.regex.RegexPatternBuilder
import io.github.homchom.recode.util.regex.regex
import net.kyori.adventure.text.Component

object ShopMessages {
    val parsers get() = arrayOf(BoosterActive)

    data class BoosterActive(val purchaserUsername: String) : ParsedMessage {
        companion object : MessageParser<Unit, BoosterActive>,
            Detector<Unit, BoosterActive> by ParsedMessage.detector(Unit)
        {
            private val regex = regex {
                boosterArrow()
                val player by username()
                str(" is using a ")
                digit.oneOrMore()
                str("x booster.")
            }

            override fun match(input: Component): BoosterActive? {
                val match = regex.matchEntirePlain(input) ?: return null
                return BoosterActive(match.groupValues[1])
            }
        }
    }
}

data class ActiveBoosterInfo(val player: String, val canTip: Boolean) {
    companion object : Detector<Unit, ActiveBoosterInfo> by detector("active booster", trial(
        ShopMessages.BoosterActive,
        Unit,
        tests = { (player), _ ->
            val subsequent = ReceiveMessageEvent.Chat.add()

            suspending s@{
                val (first) = subsequent.receive()
                val canTip = ActiveBoosterInfo.commandRegex.matchesPlain(first)

                if (!ActiveBoosterInfo.timeRegex.matchesPlain(first)) {
                    testBoolean(subsequent) { (second) ->
                        ActiveBoosterInfo.timeRegex.matchesPlain(second)
                    } ?: return@s null
                }

                ActiveBoosterInfo(player, canTip)
            }
        }
    )) {
        private val commandRegex = regex {
            boosterArrow()
            str("Use /tip to show your appreciation and receive a $TOKEN_NOTCH_CHAR token notch!")
        }
        private val timeRegex = regex {
            boosterArrow()
            str("The booster wears off in ")
            digit.oneOrMore()
            space
            anyStr("day", "hour", "minute", "second"); str("s").optional()
            period
        }
    }
}

private fun RegexPatternBuilder.boosterArrow() {
    str(BOOSTER_ARROW) * (2..3)
    space
}