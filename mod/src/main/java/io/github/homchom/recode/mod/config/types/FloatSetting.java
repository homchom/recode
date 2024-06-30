package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;

public class FloatSetting extends ConfigSetting<Float> {
    public FloatSetting() {
    }

    public FloatSetting(String key, Float defaultValue) {
        super(key, defaultValue);
    }
}
