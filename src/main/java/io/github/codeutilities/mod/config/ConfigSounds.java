package io.github.codeutilities.mod.config;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.config.types.IConfigDropdownEnum;
import io.github.codeutilities.sys.player.chat.ChatType;
import io.github.codeutilities.sys.player.chat.ChatUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum ConfigSounds implements IConfigDropdownEnum<ConfigSounds> {

    // Default
    SHIELD_BLOCK(SoundEvents.ITEM_SHIELD_BLOCK, "Shield Block"),

    // Note blocks
    BASS_DRUM(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, "Bass Drum"),
    BANJO(SoundEvents.BLOCK_NOTE_BLOCK_BANJO, "Banjo"),
    BASS(SoundEvents.BLOCK_NOTE_BLOCK_BASS, "Bass"),
    BELL(SoundEvents.BLOCK_NOTE_BLOCK_BELL, "Bell"),
    BIT(SoundEvents.BLOCK_NOTE_BLOCK_BIT, "Bit"),
    CHIME(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, "Chime"),
    COW_BELL(SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL, "Cow Bell"),
    DIDGERIDOO(SoundEvents.BLOCK_NOTE_BLOCK_DIDGERIDOO, "Didgeridoo"),
    FLUTE(SoundEvents.BLOCK_NOTE_BLOCK_FLUTE, "Flute"),
    GUITAR(SoundEvents.BLOCK_NOTE_BLOCK_GUITAR, "Guitar"),
    Harp(SoundEvents.BLOCK_NOTE_BLOCK_HARP, "Harp"),
    PLING(SoundEvents.BLOCK_NOTE_BLOCK_PLING, "Pling"),
    HAT(SoundEvents.BLOCK_NOTE_BLOCK_HAT, "Hat"),
    SNARE(SoundEvents.BLOCK_NOTE_BLOCK_SNARE, "Snare"),
    IRON_XYLOPHONE(SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, "Iron Xylophone"),
    XYLOPHONE(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE, "Xylophone"),

    // Other
    EXPERIENCE_ORB_PICKUP(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, "Experience Orb Pickup"),
    ITEM_PICKUP(SoundEvents.ENTITY_ITEM_PICKUP, "Item Pickup"),

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
