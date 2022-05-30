package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.event.*;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;

public class LegacyReceiveSoundEvent {
    private static int cancelNextSounds;

    public LegacyReceiveSoundEvent() {
        RecodeEvents.RECEIVE_SOUND.register(this::run);
    }

    private EventResult run(ClientboundSoundPacket packet) {
        if (cancelNextSounds > 0) {
            cancelNextSounds--;
            return EventResult.FAILURE;
        }
        return EventResult.PASS;
    }

    public static void cancelNextSounds(int amount) {
        cancelNextSounds = amount;
    }

    public static void cancelNextSound() {
        cancelNextSounds(1);
    }
}
