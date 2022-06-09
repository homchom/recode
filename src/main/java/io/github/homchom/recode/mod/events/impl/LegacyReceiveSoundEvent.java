package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.event.*;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;

public class LegacyReceiveSoundEvent {
    private static int cancelNextSounds;

    public LegacyReceiveSoundEvent() {
        RecodeEvents.PLAY_SOUND.listen(this::run);
    }

    private void run(EventValidator result, ClientboundSoundPacket packet) {
        if (cancelNextSounds > 0) {
            cancelNextSounds--;
            result.setValid(false);
        }
    }

    public static void cancelNextSounds(int amount) {
        cancelNextSounds = amount;
    }

    public static void cancelNextSound() {
        cancelNextSounds(1);
    }
}
