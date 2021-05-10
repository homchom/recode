package io.github.codeutilities.config.internal;

import io.github.codeutilities.config.structure.ConfigSetting;

import java.util.HashMap;
import java.util.Map;

public class ConfigInstruction {
    private final Map<String, ConfigSetting<?>> settingMap = new HashMap<>();

    public boolean isEmpty() {
        return settingMap.isEmpty();
    }

    public void put(String key, ConfigSetting<?> value) {
        this.settingMap.put(key, value);
    }

    public Map<String, ConfigSetting<?>> getSettingMap() {
        return settingMap;
    }
}
