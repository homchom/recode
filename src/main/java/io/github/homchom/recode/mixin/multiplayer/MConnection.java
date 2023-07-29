package io.github.homchom.recode.mixin.multiplayer;

import io.github.homchom.recode.multiplayer.ReceivePacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
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
        Minecraft.getInstance().execute(() -> ReceivePacketEvent.INSTANCE.run(packet));
    }
}
