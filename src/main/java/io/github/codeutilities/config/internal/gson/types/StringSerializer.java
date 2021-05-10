package io.github.codeutilities.config.internal.gson.types;

import com.google.gson.*;
import io.github.codeutilities.config.types.StringSetting;

import java.lang.reflect.Type;

public class StringSerializer implements JsonSerializer<StringSetting>, JsonDeserializer<StringSetting> {
    @Override
    public StringSetting deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) {
        String value = jsonElement.getAsString();
        StringSetting stringSetting = new StringSetting();
        stringSetting.setValue(value);
        return stringSetting;

    }

    @Override
    public JsonElement serialize(StringSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getValue());
    }
}
