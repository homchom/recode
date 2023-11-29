@file:JvmName("MultiplayerEvents")

package io.github.homchom.recode.multiplayer

import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.event.createValidatedEvent
import io.github.homchom.recode.event.wrapFabricEvent
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.kyori.adventure.text.Component
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.protocol.Packet

val JoinServerEvent = wrapFabricEvent(ClientPlayConnectionEvents.JOIN) { listener ->
    Join { handler, sender, client -> listener(ServerJoinContext(handler, sender, client)) }
}

val DisconnectFromServerEvent = wrapFabricEvent(ClientPlayConnectionEvents.DISCONNECT) { listener ->
    Disconnect { handler, client -> listener(ServerDisconnectContext(handler, client)) }
}

data class ServerJoinContext(val handler: ClientPacketListener, val sender: PacketSender, val client: Minecraft)
data class ServerDisconnectContext(val handler: ClientPacketListener, val client: Minecraft)

val ReceiveGamePacketEvent = createEvent<Packet<*>>()

val ReceiveChatMessageEvent = createValidatedEvent<Component>()

val SendCommandEvent = createValidatedEvent<String>()