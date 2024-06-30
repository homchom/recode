package io.github.homchom.recode.mod.config.internal.gson.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.homchom.recode.mod.config.types.EnumSetting;

import java.lang.reflect.Type;

public class EnumSerializer implements JsonSerializer<EnumSetting<?>> {
    @Override
    public JsonElement serialize(EnumSetting<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue().toString());
    }
}
