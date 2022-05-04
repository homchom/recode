package io.github.homchom.recode.mod.config.internal.gson;

import com.google.gson.*;
import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.config.internal.ConfigInstruction;
import io.github.homchom.recode.mod.config.structure.ConfigSetting;
import io.github.homchom.recode.mod.config.types.*;

import java.lang.reflect.Type;
import java.util.*;
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
                        setting = Recode.GSON.fromJson(primitive, StringSetting.class);
                    } else if (primitive.isBoolean()) {
                        setting = Recode.GSON.fromJson(primitive, BooleanSetting.class);
                    } else if (primitive.isNumber()) {
                        Number number = primitive.getAsNumber();
                        if (number instanceof Integer) {
                            setting = Recode.GSON.fromJson(primitive, IntegerSetting.class);
                        } else if (number instanceof Double) {
                            setting = Recode.GSON.fromJson(primitive, DoubleSetting.class);
                        } else if (number instanceof Float) {
                            setting = Recode.GSON.fromJson(primitive, FloatSetting.class);
                        } else if (number instanceof Long) {
                            setting = Recode.GSON.fromJson(primitive, LongSetting.class);
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
            System.out.println("[CuConfig] saving " + key + " (" + value + ")");
            json.add(key, Recode.GSON.toJsonTree(value));
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
