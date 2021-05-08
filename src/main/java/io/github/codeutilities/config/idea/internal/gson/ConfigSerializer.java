package io.github.codeutilities.config.idea.internal.gson;

import com.google.gson.*;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.idea.internal.ConfigInstruction;
import io.github.codeutilities.config.idea.structure.ConfigManager;
import io.github.codeutilities.config.idea.structure.ConfigSetting;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class ConfigSerializer implements JsonSerializer<ConfigInstruction>, JsonDeserializer<ConfigInstruction> {
    private static final ConfigManager CONFIG = ConfigManager.getInstance();
    private static final Class<ConfigSetting> SETTING_CLASS = ConfigSetting.class;

    @Override
    public ConfigInstruction deserialize(JsonElement obj, Type type, JsonDeserializationContext context) {
        ConfigInstruction configInstruction = new ConfigInstruction();
        if (obj.isJsonObject()) {
            JsonObject json = obj.getAsJsonObject();
            JsonObject data = json.get("settings").getAsJsonObject();

            Set<String> keys = safeSet(data);
            for (String key : keys) {
                JsonObject jsonSetting = data.get(key).getAsJsonObject();

                // Deserialize the setting
                ConfigSetting setting = CodeUtilities.GSON.fromJson(jsonSetting, SETTING_CLASS);
                configInstruction.put(key, setting);
            }

        }
        return configInstruction;
    }

    @Override
    public JsonElement serialize(ConfigInstruction obj, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        obj.getSettingMap().forEach((key, value) -> json.add(key, CodeUtilities.GSON.toJsonTree(value)));
        return json;
    }

    static Set<String> safeSet(JsonObject object) {
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            set.add(entry.getKey());
        }
        return set;
    }
}
