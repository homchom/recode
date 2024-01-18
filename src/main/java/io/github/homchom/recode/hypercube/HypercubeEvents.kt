package io.github.homchom.recode.hypercube

import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.hypercube.message.ActiveBoosterInfo
import io.github.homchom.recode.hypercube.message.StateMessages
import io.github.homchom.recode.hypercube.state.Node
import io.github.homchom.recode.hypercube.state.ipMatchesDF
import io.github.homchom.recode.hypercube.state.isOnDF
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.DisconnectFromServerEvent
import io.github.homchom.recode.multiplayer.JoinServerEvent
import io.github.homchom.recode.multiplayer.ReceiveChatMessageEvent
import io.github.homchom.recode.multiplayer.username
import io.github.homchom.recode.ui.text.matchEntirePlain
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.regex.regex
import kotlinx.coroutines.flow.map

private val patchRegex = regex {
    str("Current patch: ")
    val patch by any.oneOrMore()
    str(". See the patch notes with /patch!")
}

val JoinDFDetector = detector("DF join",
    trial(JoinServerEvent, Unit) t@{ _, _ ->
        if (isOnDF) return@t null // if already on DF, this is a node switch and should not be tested
        if (!mc.currentServer.ipMatchesDF) return@t null

        val messages = ReceiveChatMessageEvent.add()
        val tipMessage = ActiveBoosterInfo.detect(null).map(::Case).addOptional()

        val disconnect = DisconnectFromServerEvent.add()
        suspending s@{
            failOn(disconnect)

            val patch = test(messages, unlimited) { (text) ->
                patchRegex.matchEntirePlain(text)?.groupValues?.get(1)
            } ?: return@s null

            val locateMessage = StateMessages.Locate.request(mc.player!!.username, true)

            val canTip = tipMessage.any { (message) -> message?.canTip ?: false }
            JoinDFInfo(locateMessage.state.node, patch, canTip)
        }
    }
)

data class JoinDFInfo(val node: Node, val patch: String, val canTip: Boolean)