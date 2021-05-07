package io.github.codeutilities.config.idea.types;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

public class StringSetting extends ConfigSetting<String> {
    public StringSetting(String key, String defaultValue) {
        super(key, defaultValue);
    }
}
