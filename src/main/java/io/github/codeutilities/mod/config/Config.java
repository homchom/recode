package io.github.codeutilities.mod.config;

import io.github.codeutilities.mod.config.structure.ConfigManager;
import io.github.codeutilities.mod.config.structure.ConfigSetting;
import io.github.codeutilities.mod.config.types.list.ListSetting;
import net.minecraft.sound.SoundEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Config {
    private static final ConfigManager CONFIG = ConfigManager.getInstance();

    public static String getString(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, String.class);
    }

    public static String getDynamicString(String key, HashMap<String, String> vars) {
        ConfigSetting<?> setting = CONFIG.find(key);
        String value = getValue(setting, String.class);

        for (String var : vars.keySet()) {
            String val = vars.get(var);
            value = value.replaceAll("\\$\\{" + var.replaceAll("\\.", "\\.") + "}", val);
        }

        return value;
    }

    public static <T> T getEnum(String key, Class<T> enumType) {
        ConfigSetting<?> setting = CONFIG.find(key);
        return getValue(setting, enumType);
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

    public static SoundEvent getSound(String key) {
        ConfigSetting<?> setting = CONFIG.find(key);
        ListSetting<String> list = setting.cast();
        return ConfigSounds.getByName(list.getSelected());
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
