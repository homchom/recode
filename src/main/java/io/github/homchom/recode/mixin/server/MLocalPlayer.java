package io.github.homchom.recode.mixin.server;

import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.server.SendCommandEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public class MLocalPlayer {
    @Redirect(method = "sendCommand", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
    ))
    public void interceptCommandPackets(ClientPacketListener instance, Packet<?> packet) {
        if (packet instanceof ServerboundChatCommandPacket commandPacket) {
            // TODO: should this not be a validated hook?
            if (SendCommandEvent.INSTANCE.run(new SimpleValidated<>(commandPacket.command(), true))) {
                instance.send(packet);
            }
        }
    }

    @Redirect(method = "commandUnsigned", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
    ))
    public void interceptUnsignedCommandPackets(ClientPacketListener instance, Packet<?> packet) {
        interceptCommandPackets(instance, packet);
    }
}
