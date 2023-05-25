package io.github.homchom.recode.game

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.SimpleValidatedEvent
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.event.createValidatedEvent
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.network.protocol.game.ClientboundSetScorePacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object TeleportEvent :
    CustomEvent<ClientboundPlayerPositionPacket, Unit> by createEvent()

object UpdateScoreboardScoreEvent :
    CustomEvent<ClientboundSetScorePacket, Unit> by createEvent()

object PlaySoundEvent :
    SimpleValidatedEvent<ClientboundSoundPacket> by createValidatedEvent()

/**
 * A [CustomEvent] that runs when the player respawns or switches servers.
 */
object RespawnEvent :
    CustomEvent<ClientboundRespawnPacket, Unit> by createEvent()