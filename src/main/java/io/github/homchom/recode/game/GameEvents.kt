package io.github.homchom.recode.game

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.event.createEvent
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object PlaySoundEvent :
    CustomEvent<ClientboundSoundPacket, Boolean> by createEvent(),
    ValidatedEvent<ClientboundSoundPacket>