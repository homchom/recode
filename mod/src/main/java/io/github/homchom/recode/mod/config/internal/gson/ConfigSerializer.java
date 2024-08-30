package io.github.homchom.recode.mod.config.internal.gson;

import com.google.gson.*;
import io.github.homchom.recode.LegacyRecode;
import io.github.homchom.recode.mod.config.internal.ConfigInstruction;
import io.github.homchom.recode.mod.config.structure.ConfigSetting;
import io.github.homchom.recode.mod.config.types.*;

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
                        setting = LegacyRecode.GSON.fromJson(primitive, StringSetting.class);
                    } else if (primitive.isBoolean()) {
                        setting = LegacyRecode.GSON.fromJson(primitive, BooleanSetting.class);
                    } else if (primitive.isNumber()) {
                        // TODO: change this to fix number deserialization (before config rework?)
                        Number number = primitive.getAsNumber();
                        if (number instanceof Integer) {
                            setting = LegacyRecode.GSON.fromJson(primitive, IntegerSetting.class);
                        } else if (number instanceof Double) {
                            setting = LegacyRecode.GSON.fromJson(primitive, DoubleSetting.class);
                        } else if (number instanceof Float) {
                            setting = LegacyRecode.GSON.fromJson(primitive, FloatSetting.class);
                        } else if (number instanceof Long) {
                            setting = LegacyRecode.GSON.fromJson(primitive, LongSetting.class);
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
            if (value instanceof TextDescription) continue;
            json.add(key, LegacyRecode.GSON.toJsonTree(value));
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
