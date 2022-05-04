package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;

public class IntegerSetting extends ConfigSetting<Integer> {
    public IntegerSetting() {
    }

    public IntegerSetting(String key, Integer defaultValue) {
        super(key, defaultValue);
    }
}
