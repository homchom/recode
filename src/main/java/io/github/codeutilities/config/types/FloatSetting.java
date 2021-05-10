package io.github.codeutilities.config.types;

import io.github.codeutilities.config.structure.ConfigSetting;

public class FloatSetting extends ConfigSetting<Float> {
    public FloatSetting() {
    }

    public FloatSetting(String key, Float defaultValue) {
        super(key, defaultValue);
    }
}
