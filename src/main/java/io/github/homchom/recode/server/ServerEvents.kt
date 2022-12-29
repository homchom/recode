package io.github.homchom.recode.server

import io.github.homchom.recode.event.*
import io.github.homchom.recode.server.state.DFState
import io.github.homchom.recode.server.state.DFStateUpdater
import io.github.homchom.recode.util.Matchable
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

// TODO: replace Pair<..., Component> with ...
object ReceiveChatMessageEvent :
    CustomHook<Matchable<Component>, Boolean> by createHookable(),
    ValidatedHook<Matchable<Component>>

object ChangeDFStateEvent :
    CustomHook<StateChange, Unit> by DependentHook(createHookable(), DFStateUpdater),
    UnitHook<StateChange>

data class StateChange(val new: DFState?, val old: DFState?)