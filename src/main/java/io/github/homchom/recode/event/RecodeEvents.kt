package io.github.homchom.recode.event

import io.github.homchom.recode.render.UsesCustomOutlines
import io.github.homchom.recode.sys.networking.LegacyState
import io.github.homchom.recode.ui.RGBAColor
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.world.level.block.entity.BlockEntity

object RecodeEvents {
    // Game
    @JvmField val PlaySound = createValidatedEvent<ClientboundSoundPacket>()

    // Rendering
    @JvmField val RenderBlockEntity = createValidatedEvent<BlockEntity>()

    @UsesCustomOutlines
    @JvmField val OutlineBlockEntity = createEvent<BlockEntity, OutlineResult>()

    class OutlineResult {
        var outlineColor: RGBAColor? = null
    }

    // Chat
    @JvmField val ReceiveChatMessage = createValidatedEvent<Component>()

    // DF
    @JvmField val ChangeDFState = createHook<StateChange>()

    data class StateChange(val new: LegacyState, val old: LegacyState)
}