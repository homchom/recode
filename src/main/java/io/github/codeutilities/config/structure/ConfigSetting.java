package io.github.codeutilities.config.structure;

import java.util.List;

public class ConfigSetting<Value> {

    protected final String key;
    protected Value value;
    protected Value defaultValue;

    public ConfigSetting() {
        this.key = "?";
    }

    public boolean isEmpty() {
        return this.key.equals("?");
    }

    public ConfigSetting(String key, Value defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T cast() {
        return (T) this;
    }

    public boolean isAdvanced() {
        return this instanceof IAdvancedSetting;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isInteger() {
        return value instanceof Integer;
    }

    public boolean isDouble() {
        return value instanceof Double;
    }

    public boolean isFloat() {
        return value instanceof Float;
    }

    public boolean isLong() {
        return value instanceof Long;
    }

    public boolean isList() {
        return value instanceof List;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public String getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public ConfigSetting<Value> setValue(Value value) {
        this.value = value;
        return this;
    }

    public Value getDefaultValue() {
        return defaultValue;
    }
}
