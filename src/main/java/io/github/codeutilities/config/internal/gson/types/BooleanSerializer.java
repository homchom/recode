package io.github.codeutilities.config.internal.gson.types;

import com.google.gson.*;
import io.github.codeutilities.config.types.BooleanSetting;

import java.lang.reflect.Type;

public class BooleanSerializer implements JsonSerializer<BooleanSetting>, JsonDeserializer<BooleanSetting> {
    @Override
    public BooleanSetting deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        boolean value = jsonElement.getAsBoolean();
        BooleanSetting booleanSetting = new BooleanSetting();
        booleanSetting.setValue(value);
        return booleanSetting;

    }

    @Override
    public JsonElement serialize(BooleanSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getValue());
    }
}
