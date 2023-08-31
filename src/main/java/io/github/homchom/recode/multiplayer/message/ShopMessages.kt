package io.github.homchom.recode.multiplayer.message

import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.multiplayer.BOOSTER_ARROW
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.TOKEN_NOTCH_CHAR
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.ui.matchesUnstyled
import io.github.homchom.recode.util.regex.RegexPatternBuilder
import io.github.homchom.recode.util.regex.regex
import net.minecraft.network.chat.Component

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
                val match = regex.matchEntireUnstyled(input) ?: return null
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
            val subsequent = ReceiveChatMessageEvent.add()

            suspending {
                val (first) = subsequent.receive()
                val canTip = ActiveBoosterInfo.commandRegex.matchesUnstyled(first)

                if (!ActiveBoosterInfo.timeRegex.matchesUnstyled(first)) {
                    +testBoolean(subsequent) { (second) ->
                        ActiveBoosterInfo.timeRegex.matchesUnstyled(second)
                    }
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