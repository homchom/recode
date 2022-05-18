package io.github.homchom.recode.mod.mixin.game;

import io.github.homchom.recode.mod.events.impl.ServerJoinEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MServerJoin {
    private final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "handleLogin", at = @At("RETURN"), cancellable = true)
    private void handleLogin(ClientboundLoginPacket packet, CallbackInfo ci) {
        ServerJoinEvent.run(packet, ci);
    }
}