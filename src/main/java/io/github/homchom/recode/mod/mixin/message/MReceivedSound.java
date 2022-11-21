package io.github.homchom.recode.mod.mixin.message;

import io.github.homchom.recode.game.PlaySoundEvent;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MReceivedSound {
    @Inject(method = "handleSoundEvent", at = @At("HEAD"), cancellable = true)
    private void handleSoundEvent(ClientboundSoundPacket packet, CallbackInfo ci) {
        boolean playSound = PlaySoundEvent.INSTANCE.run(packet, true);
        if (!playSound) ci.cancel();
    }
}