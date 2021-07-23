package io.github.codeutilities.mod.events.impl;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ReceiveSoundEvent {

    public static int cancelNextSounds;

    public static void run(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (cancelNextSounds > 0) {
            cancelNextSounds--;
            ci.cancel();
        }
    }

    public static void cancelNextSounds(int amount) {
        cancelNextSounds = amount;
    }

    public static void cancelNextSound() {
        cancelNextSounds(1);
    }
}
