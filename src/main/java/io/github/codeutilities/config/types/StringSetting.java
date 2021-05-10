package io.github.codeutilities.config.types;

import io.github.codeutilities.config.structure.ConfigSetting;

public class StringSetting extends ConfigSetting<String> {
    public StringSetting() {
    }

    public StringSetting(String key, String defaultValue) {
        super(key, defaultValue);
    }
}
