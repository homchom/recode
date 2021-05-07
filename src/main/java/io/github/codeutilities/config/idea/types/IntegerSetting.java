package io.github.codeutilities.config.idea.types;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

public class IntegerSetting extends ConfigSetting<Integer> {
    public IntegerSetting(String key, Integer defaultValue) {
        super(key, defaultValue);
    }
}
