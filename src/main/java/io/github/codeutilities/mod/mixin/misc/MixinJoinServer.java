package io.github.codeutilities.mod.mixin.misc;

import io.github.codeutilities.mod.events.impl.ServerJoinEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinJoinServer {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Inject(method = "onGameJoin", at = @At("RETURN"), cancellable = true)
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        ServerJoinEvent.run(packet, ci);
    }
}