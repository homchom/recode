package io.github.codeutilities.config.internal.gson;

import com.google.gson.*;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.internal.ConfigInstruction;
import io.github.codeutilities.config.structure.ConfigSetting;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class ConfigSerializer implements JsonSerializer<ConfigInstruction>, JsonDeserializer<ConfigInstruction> {

    @Override
    public ConfigInstruction deserialize(JsonElement obj, Type type, JsonDeserializationContext context) {
        ConfigInstruction configInstruction = new ConfigInstruction();
        if (obj.isJsonObject()) {
            JsonObject json = obj.getAsJsonObject();
            Set<String> keys = safeSet(json);
            for (String key : keys) {
                JsonElement jsonElement = json.get(key);

                // Deserialize the setting
                ConfigSetting<?> setting = CodeUtilities.GSON.fromJson(jsonElement, ConfigSetting.class);
                configInstruction.put(key, setting);
            }

        }
        return configInstruction;
    }

    @Override
    public JsonElement serialize(ConfigInstruction obj, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        for (Map.Entry<String, ConfigSetting<?>> entry : obj.getSettingMap().entrySet()) {
            String key = entry.getKey();
            ConfigSetting<?> value = entry.getValue();
            json.add(key, CodeUtilities.GSON.toJsonTree(value));
        }
        return json;
    }

    static Set<String> safeSet(JsonObject object) {
        return object.entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
