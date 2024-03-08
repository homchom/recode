@file:JvmName("GameEvents")

package io.github.homchom.recode.game

import io.github.homchom.recode.event.createValidatedEvent
import io.github.homchom.recode.event.wrapFabricEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick
import net.minecraft.network.protocol.game.ClientboundSoundPacket

val AfterClientTickEvent = wrapFabricEvent(ClientTickEvents.END_CLIENT_TICK) { EndTick(it) }

val PlaySoundEvent = createValidatedEvent<ClientboundSoundPacket>()

val QuitGameEvent = wrapFabricEvent(ClientLifecycleEvents.CLIENT_STOPPING) { ClientStopping(it) }