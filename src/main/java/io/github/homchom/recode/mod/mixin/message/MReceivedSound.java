package io.github.homchom.recode.mod.mixin.message;

import io.github.homchom.recode.mod.events.impl.ReceiveSoundEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ALL")
@Mixin(ClientPacketListener.class)
public class MReceivedSound {
    private final Minecraft mc = Minecraft.getInstance();

    @Inject(method = "handleSoundEvent", at = @At("HEAD"), cancellable = true)
    private void handleSoundEvent(ClientboundSoundPacket packet, CallbackInfo ci) {
        ReceiveSoundEvent.run(packet, ci);
    }
}