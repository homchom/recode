package io.github.codeutilities.mod.config.types;

import io.github.codeutilities.mod.config.structure.ConfigSetting;

public class DoubleSetting extends ConfigSetting<Double> {
    public DoubleSetting() {
    }

    public DoubleSetting(String key, Double defaultValue) {
        super(key, defaultValue);
    }
}
