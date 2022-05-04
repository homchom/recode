package io.github.homchom.recode.mod.config.internal.gson.types.list;

import com.google.gson.*;
import io.github.homchom.recode.mod.config.types.list.StringListSetting;

import java.lang.reflect.Type;

public class StringListSerializer implements JsonSerializer<StringListSetting> {

    @Override
    public JsonElement serialize(StringListSetting setting, Type type, JsonSerializationContext context) {
        return new JsonPrimitive(setting.getSelected());
    }
}
