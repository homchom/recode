package io.github.homchom.recode.multiplayer.event

import io.github.homchom.recode.event.Detector
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.nullaryTrial
import io.github.homchom.recode.multiplayer.BOOSTER_ARROW_PATTERN
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.TOKEN_NOTCH_CHAR
import io.github.homchom.recode.multiplayer.USERNAME_PATTERN
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.ui.matchesUnstyled

data class ActiveBoosterMessage(val player: String, val canTip: Boolean) {
    companion object : Detector<Unit, ActiveBoosterMessage> by detector("active booster", nullaryTrial(
        ReceiveChatMessageEvent,
        tests = { (message) ->
            val player = ActiveBoosterMessage.mainRegex.matchEntireUnstyled(message)!!.groupValues[1]
            val subsequent = ReceiveChatMessageEvent.add()

            suspending {
                val (first) = subsequent.receive()
                val canTip = ActiveBoosterMessage.commandRegex.matchesUnstyled(first)

                if (!ActiveBoosterMessage.timeRegex.matchesUnstyled(first)) {
                    +testBoolean(subsequent) { (second) ->
                        ActiveBoosterMessage.timeRegex.matchesUnstyled(second)
                    }
                }

                ActiveBoosterMessage(player, canTip)
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