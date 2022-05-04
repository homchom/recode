package io.github.homchom.recode.mod.config;

import io.github.homchom.recode.mod.config.types.IConfigDropdownEnum;
import io.github.homchom.recode.sys.player.chat.*;
import net.minecraft.sounds.*;

public enum ConfigSounds implements IConfigDropdownEnum<ConfigSounds> {

    // Default
    SHIELD_BLOCK(SoundEvents.SHIELD_BLOCK, "Shield Block"),

    // Note blocks
    BASS_DRUM(SoundEvents.NOTE_BLOCK_BASEDRUM, "Bass Drum"),
    BANJO(SoundEvents.NOTE_BLOCK_BANJO, "Banjo"),
    BASS(SoundEvents.NOTE_BLOCK_BASS, "Bass"),
    BELL(SoundEvents.NOTE_BLOCK_BELL, "Bell"),
    BIT(SoundEvents.NOTE_BLOCK_BIT, "Bit"),
    CHIME(SoundEvents.NOTE_BLOCK_CHIME, "Chime"),
    COW_BELL(SoundEvents.NOTE_BLOCK_COW_BELL, "Cow Bell"),
    DIDGERIDOO(SoundEvents.NOTE_BLOCK_DIDGERIDOO, "Didgeridoo"),
    FLUTE(SoundEvents.NOTE_BLOCK_FLUTE, "Flute"),
    GUITAR(SoundEvents.NOTE_BLOCK_GUITAR, "Guitar"),
    Harp(SoundEvents.NOTE_BLOCK_HARP, "Harp"),
    PLING(SoundEvents.NOTE_BLOCK_PLING, "Pling"),
    HAT(SoundEvents.NOTE_BLOCK_HAT, "Hat"),
    SNARE(SoundEvents.NOTE_BLOCK_SNARE, "Snare"),
    IRON_XYLOPHONE(SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE, "Iron Xylophone"),
    XYLOPHONE(SoundEvents.NOTE_BLOCK_XYLOPHONE, "Xylophone"),

    // Other
    EXPERIENCE_ORB_PICKUP(SoundEvents.EXPERIENCE_ORB_PICKUP, "Experience Orb Pickup"),
    ITEM_PICKUP(SoundEvents.ITEM_PICKUP, "Item Pickup"),

    // None
    NONE(null, "None");

    private final SoundEvent sound;
    private final String name;

    ConfigSounds(SoundEvent sound, String name) {
        this.sound = sound;
        this.name = name;
    }

    public SoundEvent getSound() {
        return sound;
    }

    public String getName() {
        return name;
    }

    public ConfigSounds[] getValues() {
        return values();
    }

    public static SoundEvent getByName(String name) {
        for (ConfigSounds sounds : values()) {
            if (name.equals(sounds.getName())) {
                return sounds.getSound();
            }
        }
        ChatUtil.sendMessage("Invalid sound.", ChatType.FAIL);
        return null;
    }

}
