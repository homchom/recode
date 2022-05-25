package io.github.homchom.recode.event

import io.github.homchom.recode.mod.features.social.chat.message.Message
import io.github.homchom.recode.sys.networking.State
import net.minecraft.network.protocol.game.ClientboundSoundPacket

object RecodeEvents {
    // Game
    @JvmField
    val RECEIVE_SOUND = createEvent<(packet: ClientboundSoundPacket) -> EventResult> { listeners ->
        { packet -> handleEventWithResult(listeners, packet) }
    }

    // Chat
    @JvmField
    val RECEIVE_CHAT_MESSAGE = createEvent<(Message) -> EventResult> { listeners ->
        { message -> handleEventWithResult(listeners, message) }
    }

    // DF
    @JvmField
    val CHANGE_DF_STATE = createEvent<(new: State, old: State) -> Unit> { listeners ->
        { new, old ->
            for (listener in listeners) listener(new, old)
        }
    }
}

fun test() {
    RecodeEvents.RECEIVE_SOUND.register { packet ->
        println("received sound ${packet.sound}")
        EventResult.PASS
    }
}