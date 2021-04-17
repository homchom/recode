package io.github.codeutilities.config;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum ConfigSounds {

    ShieldBlock(SoundEvents.ITEM_SHIELD_BLOCK, "Shield Block"),

    BassDrum(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, "Bass Drum"),
    Banjo(SoundEvents.BLOCK_NOTE_BLOCK_BANJO, "Banjo"),
    Bass(SoundEvents.BLOCK_NOTE_BLOCK_BASS, "Bass"),
    Bell(SoundEvents.BLOCK_NOTE_BLOCK_BELL, "Bell"),
    Bit(SoundEvents.BLOCK_NOTE_BLOCK_BIT, "Bit"),
    Chime(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, "Chime"),
    CowBell(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, "Cow Bell"),
    Didgeridoo(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, "Didgeridoo"),
    Flute(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, "Flute"),
    Guitar(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, "Guitar"),
    Harp(SoundEvents.BLOCK_NOTE_BLOCK_HARP, "Harp"),
    Pling(SoundEvents.BLOCK_NOTE_BLOCK_PLING, "Pling"),
    Hat(SoundEvents.BLOCK_NOTE_BLOCK_HAT, "Hat"),
    Snare(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, "Snare"),
    IronXylophone(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, "Iron Xylophone"),
    Xylophone(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, "Xylophone"),

    ExperienceOrbPickup(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, "Experience Orb Pickup"),

    ItemPickup(SoundEvents.ENTITY_ITEM_PICKUP, "Item Pickup"),

    // ====================

    None(null, "None");

    private final SoundEvent sound;
    private final String name;

    ConfigSounds(SoundEvent sound, String name) {
        this.sound = sound;
        this.name = name;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public String getName() { return name; }

    public static SoundEvent getSoundFromName(String name) {
        for (ConfigSounds sounds : values()) {
            if (name.equals(sounds.getName())) {
                return sounds.getSound();
            }
        }
        return null;
    }

}
