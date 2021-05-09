package io.github.codeutilities.config.internal.gson.types;

import com.google.gson.*;
import io.github.codeutilities.config.types.FloatSetting;

import java.lang.reflect.Type;

public class FloatSerializer implements JsonSerializer<FloatSetting>, JsonDeserializer<FloatSetting> {
    @Override
    public FloatSetting deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        Number number = jsonElement.getAsNumber();
        float value = number.floatValue();
        FloatSetting floatSetting = new FloatSetting();
        floatSetting.setValue(value);
        return floatSetting;
    }

    @Override
    public JsonElement serialize(FloatSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getValue());
    }
}
