package io.github.codeutilities.config.types;

import io.github.codeutilities.config.structure.ConfigSetting;

public class IntegerSetting extends ConfigSetting<Integer> {
    public IntegerSetting() {
    }

    public IntegerSetting(String key, Integer defaultValue) {
        super(key, defaultValue);
    }
}
