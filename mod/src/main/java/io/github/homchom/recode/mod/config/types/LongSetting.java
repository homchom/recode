package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.structure.ConfigSetting;

public class LongSetting extends ConfigSetting<Long> {
    public LongSetting() {
    }

    public LongSetting(String key, Long defaultValue) {
        super(key, defaultValue);
    }
}
