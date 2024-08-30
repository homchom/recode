@file:JvmName("MultiplayerEvents")

package io.github.homchom.recode.multiplayer

import com.google.common.cache.CacheBuilder
import io.github.homchom.recode.event.*
import io.github.homchom.recode.ui.text.VanillaComponent
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.kyori.adventure.text.Component
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener
import net.minecraft.network.protocol.Packet
import java.time.Duration as JDuration

val JoinServerEvent = wrapFabricEvent(ClientPlayConnectionEvents.JOIN) { listener ->
    Join { handler, sender, client -> listener(ServerJoinContext(handler, sender, client)) }
}

val DisconnectFromServerEvent = wrapFabricEvent(ClientPlayConnectionEvents.DISCONNECT) { listener ->
    Disconnect { handler, client -> listener(ServerDisconnectContext(handler, client)) }
}

data class ServerJoinContext(val handler: ClientPacketListener, val sender: PacketSender, val client: Minecraft)
data class ServerDisconnectContext(val handler: ClientPacketListener, val client: Minecraft)

val ReceivePacketEvent = createEvent<Packet<*>>()

val SendPacketEvent = createEvent<Packet<*>>()

sealed class ReceiveMessageEvent : CustomEvent<SimpleValidated<Component>, Boolean> by createValidatedEvent() {
    data object Chat : ReceiveMessageEvent()
    data object ActionBar : ReceiveMessageEvent()

    private val cache = CacheBuilder.newBuilder()
        .expireAfterAccess(JDuration.ofSeconds(1))
        .build<VanillaComponent, Component>()

    /**
     * First converts [vanillaText] to a [Component], caching the result. Then [run]s the event.
     */
    fun cacheAndRun(vanillaText: VanillaComponent): Boolean {
        val text = cache.get(vanillaText) { vanillaText.asComponent() }
        return run(SimpleValidated(text))
    }
}