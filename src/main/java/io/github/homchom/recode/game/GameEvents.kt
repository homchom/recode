@file:JvmName("GameEvents")

package io.github.homchom.recode.game

import io.github.homchom.recode.event.createEvent
import io.github.homchom.recode.event.createValidatedEvent
import net.minecraft.network.protocol.game.ClientboundSoundPacket

val PlaySoundEvent = createValidatedEvent<ClientboundSoundPacket>()

val QuitGameEvent = createEvent<Unit>()