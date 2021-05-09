package io.github.codeutilities.config.internal.gson.types;

import com.google.gson.*;
import io.github.codeutilities.config.types.LongSetting;

import java.lang.reflect.Type;

public class LongSerializer implements JsonSerializer<LongSetting>, JsonDeserializer<LongSetting> {
    @Override
    public LongSetting deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        Number number = jsonElement.getAsNumber();
        long value = number.longValue();
        LongSetting longSetting = new LongSetting();
        longSetting.setValue(value);
        return longSetting;
    }

    @Override
    public JsonElement serialize(LongSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getValue());
    }
}
