package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.ConfigSounds;

public class SoundSetting extends DropdownSetting<ConfigSounds> {

    public SoundSetting(String key) {
        super(key, DropdownSetting.fromEnum(ConfigSounds.NONE));
    }
}
