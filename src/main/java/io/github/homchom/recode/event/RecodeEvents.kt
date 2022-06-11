package io.github.homchom.recode.event

import io.github.homchom.recode.mod.features.social.chat.message.Message
import io.github.homchom.recode.render.GlobalUsesCustomOutlines
import io.github.homchom.recode.render.RGBA
import io.github.homchom.recode.sys.networking.DFState
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.world.level.block.entity.BlockEntity

object RecodeEvents {
    // Game
    @JvmField val PLAY_SOUND = createValidatedEvent<ClientboundSoundPacket>()

    // Rendering
    @JvmField val RENDER_BLOCK_ENTITY = createValidatedEvent<BlockEntity>()

    @GlobalUsesCustomOutlines
    @JvmField val OUTLINE_BLOCK_ENTITY = createEvent<BlockEntity, OutlineResult>()

    class OutlineResult {
        var outlineColor: RGBA? = null
    }

    // Chat
    @JvmField val RECEIVE_CHAT_MESSAGE = createValidatedEvent<Message>()

    // DF
    @JvmField val CHANGE_DF_STATE = createHook<StateChange>()

    data class StateChange(val new: DFState, val old: DFState)
}