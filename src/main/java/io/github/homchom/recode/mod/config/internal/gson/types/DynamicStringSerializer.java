package io.github.homchom.recode.mod.config.internal.gson.types;

import com.google.gson.*;
import io.github.homchom.recode.mod.config.types.DynamicStringSetting;

import java.lang.reflect.Type;

public class DynamicStringSerializer implements JsonSerializer<DynamicStringSetting> {

    @Override
    public JsonElement serialize(DynamicStringSetting src, Type typeOfSrc,
        JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }
}
