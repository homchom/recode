package io.github.homchom.recode.mixin.multiplayer;

import io.github.homchom.recode.multiplayer.MultiplayerEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Connection.class)
public abstract class MConnection {
    @Inject(method = "genericsFtw", at = @At("HEAD"))
    private static <T extends PacketListener> void interceptPacketHandling(
            Packet<T> packet,
            PacketListener packetListener,
            CallbackInfo ci
    ) {
        if (packetListener instanceof ClientGamePacketListener) {
            Minecraft.getInstance().execute(() -> MultiplayerEvents.getReceiveGamePacketEvent().run(packet));
        }
    }
}
