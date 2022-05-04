package io.github.homchom.recode.mod.config.internal.gson.types;

import com.google.gson.*;
import io.github.homchom.recode.mod.config.types.SoundSetting;

import java.lang.reflect.Type;

public class SoundSerializer implements JsonSerializer<SoundSetting> {

    @Override
    public JsonElement serialize(SoundSetting src, Type typeOfSrc,
        JsonSerializationContext context) {
        return new JsonPrimitive(src.getSelected());
    }
}
