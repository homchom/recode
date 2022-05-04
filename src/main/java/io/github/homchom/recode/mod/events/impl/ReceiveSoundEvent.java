package io.github.homchom.recode.mod.events.impl;

import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ReceiveSoundEvent {

    public static int cancelNextSounds;

    public static void run(ClientboundSoundPacket packet, CallbackInfo ci) {
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
