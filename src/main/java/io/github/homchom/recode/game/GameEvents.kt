@file:JvmName("GameEvents")

package io.github.homchom.recode.game

import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.event.createValidatedEvent
import io.github.homchom.recode.event.run
import io.github.homchom.recode.event.wrapFabricEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick
import net.minecraft.network.protocol.game.ClientboundSoundPacket

val AfterClientTickEvent = wrapFabricEvent(ClientTickEvents.END_CLIENT_TICK) { EndTick(it) }

val PlaySoundEvent = createValidatedEvent<ClientboundSoundPacket>()

/**
 * An event that runs when the client stops, including crashes.
 */
val GameStopEvent = createEvent<Unit>().also { event ->
    ClientLifecycleEvents.CLIENT_STOPPING.register { event.run() }
}