package io.github.codeutilities.config.types;

import io.github.codeutilities.config.structure.ConfigSetting;

public class DoubleSetting extends ConfigSetting<Double> {
    public DoubleSetting() {
    }

    public DoubleSetting(String key, Double defaultValue) {
        super(key, defaultValue);
    }
}
