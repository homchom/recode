package io.github.codeutilities.config.internal.gson;

import com.google.gson.*;
import io.github.codeutilities.config.structure.ConfigSetting;
import io.github.codeutilities.config.types.*;
import io.github.codeutilities.config.types.hud.HudData;
import io.github.codeutilities.config.types.hud.PositionSetting;

import java.lang.reflect.Type;

public class ConfigSettingSerializer implements JsonSerializer<ConfigSetting<?>>, JsonDeserializer<ConfigSetting<?>> {
    @Override
    public ConfigSetting<?> deserialize(JsonElement obj, Type type, JsonDeserializationContext context) {

        // Deserialization of more advanced settings like, hud position ect
        if (obj instanceof JsonObject) {
            JsonObject json = (JsonObject) obj;
            String settingType = json.get("type").getAsString();

            // Hud positions
            if (settingType.equals("position")) {
                int x = json.get("x").getAsInt();
                int y = json.get("y").getAsInt();

                HudData hudData = new HudData(x, y);
                return new PositionSetting().setValue(hudData);
            }
        }

        // Deserialization of basic primitives
        if (obj instanceof JsonPrimitive) {
            JsonPrimitive primitive = obj.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                boolean asBoolean = primitive.getAsBoolean();
                return new BooleanSetting().setValue(asBoolean);
            }
            if (primitive.isString()) {
                String value = primitive.getAsString();
                return new StringSetting().setValue(value);
            }

            // Numbers
            if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();
                if (number instanceof Integer) {
                    int value = number.intValue();
                    return new IntegerSetting().setValue(value);
                }
                if (number instanceof Double) {
                    double value = number.doubleValue();
                    return new DoubleSetting().setValue(value);
                }
                if (number instanceof Long) {
                    long value = number.longValue();
                    return new LongSetting().setValue(value);
                }
                if (number instanceof Float) {
                    float value = number.floatValue();
                    return new FloatSetting().setValue(value);
                }
            }
        }
        return null;
    }

    @Override
    public JsonElement serialize(ConfigSetting<?> obj, Type type, JsonSerializationContext context) {
        // Serialization of more advanced settings
        if (obj.isAdvanced()) {
            // Advanced settings are always serialized into a json object
            JsonObject json = new JsonObject();
            String settingType = "?";

            // Hud positions
            if (obj instanceof PositionSetting) {
                settingType = "position";

                PositionSetting positionSetting = obj.cast();
                HudData value = positionSetting.getValue();
                json.addProperty("x", value.getX());
                json.addProperty("y", value.getY());
            }

            json.addProperty("type", settingType);
            return json;
        }

        // Serialization of lists
        if (obj.isList()) {
            ListSetting<?> listSetting = obj.cast();
            if (listSetting.isString()) {
                ListSetting<String> setting = listSetting.cast();
                return new JsonPrimitive(setting.getSelected());
            }
        }

        // Serialization of basic primitives
        if (obj.isString()) {
            StringSetting setting = obj.cast();
            return new JsonPrimitive(setting.getValue());
        }
        if (obj.isBoolean()) {
            BooleanSetting setting = obj.cast();
            return new JsonPrimitive(setting.getValue());
        }
        if (obj.isInteger()) {
            IntegerSetting setting = obj.cast();
            return new JsonPrimitive(setting.getValue());
        }
        if (obj.isFloat()) {
            FloatSetting setting = obj.cast();
            return new JsonPrimitive(setting.getValue());
        }
        if (obj.isDouble()) {
            DoubleSetting setting = obj.cast();
            return new JsonPrimitive(setting.getValue());
        }
        if (obj.isLong()) {
            LongSetting setting = obj.cast();
            return new JsonPrimitive(setting.getValue());
        }

        return null;
    }
}
