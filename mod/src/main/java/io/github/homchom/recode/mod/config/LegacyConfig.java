package io.github.homchom.recode.mod.config;

import io.github.homchom.recode.mod.config.structure.ConfigManager;
import io.github.homchom.recode.mod.config.structure.ConfigSetting;
import io.github.homchom.recode.mod.config.types.list.ListSetting;
import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Map;

public class LegacyConfig {
    private static ConfigManager config = ConfigManager.getInstance();

    public static String getString(String key) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, String.class);
    }

    public static String getDynamicString(String key, Map<String, String> vars) {
        ConfigSetting<?> setting = config.find(key);
        String value = getValue(setting, String.class);

        for (String var : vars.keySet()) {
            String val = vars.get(var);
            value = value.replaceAll("\\$\\{" + var.replaceAll("\\.", "\\.") + "}", val);
        }

        return value;
    }

    public static <T extends Enum<T>> T getEnum(String key, Class<T> enumType) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, enumType);
    }

    public static Double getDouble(String key) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, Double.class);
    }

    public static Integer getInteger(String key) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, Integer.class);
    }

    public static Float getFloat(String key) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, Float.class);
    }

    public static Boolean getBoolean(String key) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, Boolean.class);
    }

    public static Long getLong(String key) {
        ConfigSetting<?> setting = config.find(key);
        return getValue(setting, Long.class);
    }

    public static SoundEvent getSound(String key) {
        ConfigSetting<?> setting = config.find(key);
        ListSetting<String> list = setting.cast();
        return ConfigSounds.getByName(list.getSelected());
    }

    @SuppressWarnings("unchecked")
    public static List<String> getStringList(String key) {
        ConfigSetting<?> setting = config.find(key);
        return (List<String>) getValue(setting, List.class);
    }

    @SuppressWarnings("unchecked")
    public static <Value> Value getValue(ConfigSetting<?> setting, Class<Value> valueClass) {
        if (setting == null) return null;
        Object value = setting.getValue();
        if (valueClass.isAssignableFrom(value.getClass())) {
            return (Value) setting.getValue();
        }
        return null;
    }
}
