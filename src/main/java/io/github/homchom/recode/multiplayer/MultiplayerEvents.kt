package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.*
import io.github.homchom.recode.mc
import io.github.homchom.recode.multiplayer.state.*
import io.github.homchom.recode.ui.matchEntireUnstyled
import kotlinx.coroutines.async
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.chat.Component

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

private val patchRegex = Regex("""Current patch: (.+). See the patch notes with /patch!""")

object JoinDFDetector :
    Detector<Unit, JoinDFInfo> by detector(nullaryTrial(JoinServerEvent) {
        requireFalse(isOnDF) // if already on DF, this is a node switch and should not be tested
        requireTrue(ipMatchesDF)

        val messages = ReceiveChatMessageEvent.add()
        val tipMessage = TipMessage.detect(null).add()

        val disconnect = DisconnectFromServerEvent.add()
        suspending {
            failOn(disconnect)

            val patch = +test(messages, unlimited) { (text) ->
                tipMessage.receive()
                patchRegex.matchEntireUnstyled(text)?.groupValues?.get(1)
            }

            val canTip = async {
                testBoolean(tipMessage) { it?.canTip ?: false }.passed
            }
            val request = HideableStateRequest(mc.player!!.username, true)
            val message = LocateMessage.request(request)

            JoinDFInfo(message.state.node, patch, canTip.await())
        }
    })

data class JoinDFInfo(val node: Node, val patch: String, val canTip: Boolean)

object ReceiveChatMessageEvent :
    SimpleValidatedEvent<Component> by createValidatedEvent()

object SendCommandEvent :
    SimpleValidatedEvent<String> by createValidatedEvent()