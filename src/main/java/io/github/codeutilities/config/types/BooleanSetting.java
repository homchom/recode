package io.github.codeutilities.config.types;

import io.github.codeutilities.config.structure.ConfigSetting;

public class BooleanSetting extends ConfigSetting<Boolean> {
    public BooleanSetting() {
    }

    public BooleanSetting(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }
}
