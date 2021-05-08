package io.github.codeutilities.config.idea.types;

import io.github.codeutilities.config.idea.structure.ConfigSetting;

import java.util.Arrays;
import java.util.List;

public class ListSetting<Type> extends ConfigSetting<List<Type>> {
    private String listKey = "";

    @SuppressWarnings("unchecked")
    public ListSetting(String key, Type... defaultValue) {
        super(key, Arrays.asList(defaultValue));
    }

    public String getListKey() {
        return listKey;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T cast() {
        return (T) this;
    }

    @Override
    public boolean isString() {
        return defaultValue.get(0) instanceof String;
    }

    @Override
    public boolean isInteger() {
        return defaultValue.get(0) instanceof Integer;
    }

    @Override
    public boolean isDouble() {
        return defaultValue.get(0) instanceof Double;
    }

    @Override
    public boolean isFloat() {
        return defaultValue.get(0) instanceof Float;
    }

    @Override
    public boolean isLong() {
        return defaultValue.get(0) instanceof Long;
    }

    @Override
    public boolean isList() {
        return defaultValue.get(0) instanceof List;
    }

    @Override
    public boolean isBoolean() {
        return defaultValue.get(0) instanceof Boolean;
    }
}
