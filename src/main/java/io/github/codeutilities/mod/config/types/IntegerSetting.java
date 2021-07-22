package io.github.codeutilities.mod.config.types;

import io.github.codeutilities.mod.config.structure.ConfigSetting;

public class IntegerSetting extends ConfigSetting<Integer> {
    public IntegerSetting() {
    }

    public IntegerSetting(String key, Integer defaultValue) {
        super(key, defaultValue);
    }
}
