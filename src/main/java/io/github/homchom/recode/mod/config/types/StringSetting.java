package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;

public class StringSetting extends ConfigSetting<String> {
    public StringSetting() {
    }

    public StringSetting(String key) {
        super(key, "");
    }

    public StringSetting(String key, String defaultValue) {
        super(key, defaultValue);
    }
}
