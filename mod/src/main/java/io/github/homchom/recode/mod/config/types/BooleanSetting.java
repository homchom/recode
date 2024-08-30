package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;

public class BooleanSetting extends ConfigSetting<Boolean> {
    public BooleanSetting() {
    }

    public BooleanSetting(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }
}
