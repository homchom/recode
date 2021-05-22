package io.github.codeutilities.config.internal.gson;

import com.google.gson.*;
import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.config.internal.ConfigInstruction;
import io.github.codeutilities.config.structure.ConfigSetting;
import io.github.codeutilities.config.types.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigSerializer implements JsonSerializer<ConfigInstruction>, JsonDeserializer<ConfigInstruction> {

    @Override
    public ConfigInstruction deserialize(JsonElement obj, Type type, JsonDeserializationContext context) {
        ConfigInstruction configInstruction = new ConfigInstruction();
        if (obj.isJsonObject()) {
            JsonObject json = obj.getAsJsonObject();
            Set<String> keys = getSafeKeys(json);

            for (String key : keys) {
                JsonElement jsonElement = json.get(key);
                ConfigSetting<?> setting = null;

                // Deserialize the setting
                if (jsonElement.isJsonPrimitive()) {
                    JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();

                    if (primitive.isString()) {
                        setting = CodeUtilities.GSON.fromJson(primitive, StringSetting.class);
                    } else if (primitive.isBoolean()) {
                        setting = CodeUtilities.GSON.fromJson(primitive, BooleanSetting.class);
                    } else if (primitive.isNumber()) {
                        Number number = primitive.getAsNumber();
                        if (number instanceof Integer) {
                            setting = CodeUtilities.GSON.fromJson(primitive, IntegerSetting.class);
                        } else if (number instanceof Double) {
                            setting = CodeUtilities.GSON.fromJson(primitive, DoubleSetting.class);
                        } else if (number instanceof Float) {
                            setting = CodeUtilities.GSON.fromJson(primitive, FloatSetting.class);
                        } else if (number instanceof Long) {
                            setting = CodeUtilities.GSON.fromJson(primitive, LongSetting.class);
                        }
                    }
                }

                if (setting != null) configInstruction.put(key, setting);
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

    static Set<String> getSafeKeys(JsonObject object) {
        return object.entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
