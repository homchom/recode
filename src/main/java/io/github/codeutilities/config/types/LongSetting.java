package io.github.codeutilities.config.types;

import io.github.codeutilities.config.structure.ConfigSetting;

public class LongSetting extends ConfigSetting<Long> {
    public LongSetting() {
    }

    public LongSetting(String key, Long defaultValue) {
        super(key, defaultValue);
    }
}
