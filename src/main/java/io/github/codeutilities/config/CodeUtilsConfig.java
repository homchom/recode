package io.github.codeutilities.config;

import io.github.codeutilities.config.structure.ConfigManager;
import io.github.codeutilities.config.structure.ConfigSetting;

import java.util.List;
import java.util.Objects;

public class CodeUtilsConfig {
    private static final ConfigManager CONFIG = ConfigManager.getInstance();

    public static String getString(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, String.class);
    }

    public static Double getDouble(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, Double.class);
    }

    public static Integer getInteger(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, Integer.class);
    }

    public static Float getFloat(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, Float.class);
    }

    public static Boolean getBoolean(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, Boolean.class);
    }

    public static Long getLong(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, Long.class);
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringList(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return (List<String>) getValue(setting, List.class);
    }

    @SuppressWarnings("unchecked")
    public static <Value> Value getValue(ConfigSetting<?> setting, Class<Value> valueClass) {
        Objects.requireNonNull(setting, "Could not find the setting");
        Object value = setting.getValue();
        if (value.getClass().isAssignableFrom(valueClass)) {
            return (Value) setting.getValue();
        }
        return null;
    }
}
