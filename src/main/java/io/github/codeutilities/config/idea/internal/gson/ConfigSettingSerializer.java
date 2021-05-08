package io.github.codeutilities.config.idea.internal.gson;

import com.google.gson.*;
import io.github.codeutilities.config.idea.structure.ConfigSetting;

import java.lang.reflect.Type;

public class ConfigSettingSerializer implements JsonSerializer<ConfigSetting<?>>, JsonDeserializer<ConfigSetting<?>> {
    @Override
    public ConfigSetting<?> deserialize(JsonElement obj, Type type, JsonDeserializationContext context) {
        return null;
    }

    @Override
    public JsonElement serialize(ConfigSetting<?> obj, Type type, JsonSerializationContext context) {
        return null;
    }
}
