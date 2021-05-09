package io.github.codeutilities.config.internal.gson.types;

import com.google.gson.*;
import io.github.codeutilities.config.types.DoubleSetting;

import java.lang.reflect.Type;

public class DoubleSerializer implements JsonSerializer<DoubleSetting>, JsonDeserializer<DoubleSetting> {
    @Override
    public DoubleSetting deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        Number number = jsonElement.getAsNumber();
        double value = number.doubleValue();
        DoubleSetting doubleSetting = new DoubleSetting();
        doubleSetting.setValue(value);
        return doubleSetting;

    }

    @Override
    public JsonElement serialize(DoubleSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getValue());
    }
}
