package io.github.homchom.recode.game

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.SimpleValidatedEvent
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.event.createValidatedEvent
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object ItemSlotUpdateEvent :
    CustomEvent<ClientboundContainerSetSlotPacket, Unit> by createEvent()

object PlaySoundEvent :
    SimpleValidatedEvent<ClientboundSoundPacket> by createValidatedEvent()