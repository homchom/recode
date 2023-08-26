package io.github.homchom.recode.game

import io.github.homchom.recode.event.CustomEvent
import io.github.homchom.recode.event.SimpleValidatedEvent
import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.event.createValidatedEvent
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object PlaySoundEvent :
    SimpleValidatedEvent<ClientboundSoundPacket> by createValidatedEvent()

object QuitGameEvent :
    CustomEvent<Unit, Unit> by createEvent()