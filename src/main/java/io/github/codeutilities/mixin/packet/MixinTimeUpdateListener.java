package io.github.codeutilities.mixin.packet;

import io.github.codeutilities.util.TPSUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinTimeUpdateListener {

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"), cancellable = true)
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TPSUtil.calculateTps(System.currentTimeMillis());
    }
}
