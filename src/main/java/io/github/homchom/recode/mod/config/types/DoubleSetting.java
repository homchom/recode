package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;

public class DoubleSetting extends ConfigSetting<Double> {
    public DoubleSetting() {
    }

    public DoubleSetting(String key, Double defaultValue) {
        super(key, defaultValue);
    }
}
