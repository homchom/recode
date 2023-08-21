package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.*
import io.github.homchom.recode.event.trial.detector
import io.github.homchom.recode.event.trial.trial
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.message.ActiveBoosterInfo
import io.github.homchom.recode.multiplayer.message.StateMessages
import io.github.homchom.recode.multiplayer.state.Node
import io.github.homchom.recode.multiplayer.state.ipMatchesDF
import io.github.homchom.recode.multiplayer.state.isOnDF
import io.github.homchom.recode.ui.matchEntireUnstyled
import io.github.homchom.recode.util.Case
import io.github.homchom.recode.util.regex.regex
import kotlinx.coroutines.flow.map
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet

object JoinServerEvent :
    WrappedEvent<ServerJoinContext, Join> by
        wrapFabricEvent(ClientPlayConnectionEvents.JOIN, { listener ->
            Join { handler, sender, client -> listener(ServerJoinContext(handler, sender, client)) }
        })

object DisconnectFromServerEvent :
    WrappedEvent<ServerDisconnectContext, Disconnect> by
        wrapFabricEvent(ClientPlayConnectionEvents.DISCONNECT, { listener ->
            Disconnect { handler, client -> listener(ServerDisconnectContext(handler, client)) }
        })

data class ServerJoinContext(val handler: ClientPacketListener, val sender: PacketSender, val client: Minecraft)
data class ServerDisconnectContext(val handler: ClientPacketListener, val client: Minecraft)

object ReceiveGamePacketEvent :
    CustomEvent<Packet<*>, Unit> by createEvent()

private val patchRegex = regex {
    str("Current patch: ")
    val patch by any.oneOrMore()
    str(". See the patch notes with /patch!")
}

object JoinDFDetector :
    Detector<Unit, JoinDFInfo> by detector("DF join",
        trial(JoinServerEvent, Unit) { _, _ ->
            requireFalse(isOnDF) // if already on DF, this is a node switch and should not be tested
            requireTrue(mc.currentServer.ipMatchesDF)

            val messages = ReceiveChatMessageEvent.add()
            val tipMessage = ActiveBoosterInfo.detect(null).map(::Case).addOptional()

            val disconnect = DisconnectFromServerEvent.add()
            suspending {
                failOn(disconnect)

                val patch = +test(messages, unlimited) { (text) ->
                    patchRegex.matchEntireUnstyled(text)?.groupValues?.get(1)
                }

                val locateMessage = StateMessages.Locate.request(mc.player!!.username, true)

                val canTip = tipMessage.any { (message) -> message?.canTip ?: false }
                JoinDFInfo(locateMessage.state.node, patch, canTip)
            }
        })

data class JoinDFInfo(val node: Node, val patch: String, val canTip: Boolean)

object ReceiveChatMessageEvent :
    SimpleValidatedEvent<Component> by createValidatedEvent()

object SendCommandEvent :
    SimpleValidatedEvent<String> by createValidatedEvent()