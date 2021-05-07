package io.github.codeutilities.config.idea.types;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

public class DoubleSetting extends ConfigSetting<Double> {
    public DoubleSetting(String key, Double defaultValue) {
        super(key, defaultValue);
    }
}
