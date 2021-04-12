package io.github.codeutilities.config;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum NoteSounds {
    None(null),
    BassDrum(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM),
    Banjo(SoundEvents.BLOCK_NOTE_BLOCK_BANJO),
    Bass(SoundEvents.BLOCK_NOTE_BLOCK_BASS),
    Bell(SoundEvents.BLOCK_NOTE_BLOCK_BELL),
    Bit(SoundEvents.BLOCK_NOTE_BLOCK_BIT),
    Chime(SoundEvents.BLOCK_NOTE_BLOCK_CHIME),
    CowBell(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL),
    Didgeridoo(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO),
    Flute(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE),
    Guitar(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR),
    Harp(SoundEvents.BLOCK_NOTE_BLOCK_HARP),
    Pling(SoundEvents.BLOCK_NOTE_BLOCK_PLING),
    Hat(SoundEvents.BLOCK_NOTE_BLOCK_HAT),
    Snare(SoundEvents.BLOCK_NOTE_BLOCK_SNARE),
    IronXylophone(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE),
    Xylophone(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE);

    private final SoundEvent sound;

    NoteSounds(SoundEvent sound) {
        this.sound = sound;
    }

    public SoundEvent getSound() {
        return sound;
    }

}
