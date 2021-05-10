package io.github.codeutilities.config.internal.gson.types.list;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.codeutilities.config.types.list.StringListSetting;

import java.lang.reflect.Type;

public class StringListSerializer implements JsonSerializer<StringListSetting> {

    @Override
    public JsonElement serialize(StringListSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getSelected());
    }
}
