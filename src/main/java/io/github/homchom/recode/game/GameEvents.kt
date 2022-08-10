package io.github.homchom.recode.game

import io.github.homchom.recode.event.CustomListenable
import io.github.homchom.recode.event.ValidatedEvent
import io.github.homchom.recode.event.createEvent
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object PlaySoundEvent :
    CustomListenable<ClientboundSoundPacket, Boolean> by createEvent(),
    ValidatedEvent<ClientboundSoundPacket>