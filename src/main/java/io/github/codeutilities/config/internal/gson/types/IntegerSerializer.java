package io.github.codeutilities.config.internal.gson.types;

import com.google.gson.*;
import io.github.codeutilities.config.types.IntegerSetting;

import java.lang.reflect.Type;

public class IntegerSerializer implements JsonSerializer<IntegerSetting>, JsonDeserializer<IntegerSetting> {
    @Override
    public IntegerSetting deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        Number number = jsonElement.getAsNumber();
        int value = number.intValue();
        IntegerSetting integerSetting = new IntegerSetting();
        integerSetting.setValue(value);
        return integerSetting;

    }

    @Override
    public JsonElement serialize(IntegerSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getValue());
    }
}
