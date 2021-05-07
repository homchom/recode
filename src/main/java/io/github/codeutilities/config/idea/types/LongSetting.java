package io.github.codeutilities.config.idea.types;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

public class LongSetting extends ConfigSetting<Long> {
    public LongSetting(String key, Long defaultValue) {
        super(key, defaultValue);
    }
}
