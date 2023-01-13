package io.github.homchom.recode.server

import io.github.homchom.recode.event.*
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.chat.Component

object JoinServerEvent :
    WrappedHook<ServerJoinContext, Unit, Join> by
        wrapFabricEvent(ClientPlayConnectionEvents.JOIN, { listener ->
            Join { handler, sender, client -> listener(ServerJoinContext(handler, sender, client), Unit) }
        })

object DisconnectFromServerEvent :
    WrappedHook<ServerDisconnectContext, Unit, Disconnect> by
        wrapFabricEvent(ClientPlayConnectionEvents.DISCONNECT, { listener ->
            Disconnect { handler, client -> listener(ServerDisconnectContext(handler, client), Unit) }
        })

data class ServerJoinContext(val handler: ClientPacketListener, val sender: PacketSender, val client: Minecraft)
data class ServerDisconnectContext(val handler: ClientPacketListener, val client: Minecraft)

object ReceiveChatMessageEvent :
    CustomHook<Component, Boolean> by createHook(),
    ValidatedHook<Component>