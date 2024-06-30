package io.github.homchom.recode.mod.config.internal.gson.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.homchom.recode.mod.config.types.DynamicStringSetting;

import java.lang.reflect.Type;

public class DynamicStringSerializer implements JsonSerializer<DynamicStringSetting> {

    @Override
    public JsonElement serialize(DynamicStringSetting src, Type typeOfSrc,
        JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }
}
