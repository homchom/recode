package io.github.codeutilities.mod.config.types;

import io.github.codeutilities.mod.config.structure.ConfigSetting;

public class FloatSetting extends ConfigSetting<Float> {
    public FloatSetting() {
    }

    public FloatSetting(String key, Float defaultValue) {
        super(key, defaultValue);
    }
}
