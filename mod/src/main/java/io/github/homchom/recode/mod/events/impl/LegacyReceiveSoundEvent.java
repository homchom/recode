package io.github.homchom.recode.mod.events.impl;

import io.github.homchom.recode.event.SimpleValidated;
import io.github.homchom.recode.game.GameEvents;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;

public class LegacyReceiveSoundEvent {
    private static int cancelNextSounds;

    public LegacyReceiveSoundEvent() {
        GameEvents.getPlaySoundEvent().register(this::run);
    }

    private void run(SimpleValidated<ClientboundSoundPacket> context) {
        if (cancelNextSounds > 0) {
            cancelNextSounds--;
            context.invalidate();
        }
    }

    public static void cancelNextSounds(int amount) {
        cancelNextSounds = amount;
    }

    public static void cancelNextSound() {
        cancelNextSounds(1);
    }
}
