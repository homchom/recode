package io.github.codeutilities.config.types.list;

import io.github.codeutilities.config.structure.ConfigSetting;

import java.util.Arrays;
import java.util.List;

public class ListSetting<Type> extends ConfigSetting<List<Type>> {
    private Type selected;

    public ListSetting() {
    }

    @SuppressWarnings("unchecked")
    public ListSetting(String key, Type... defaultValue) {
        super(key, Arrays.asList(defaultValue));
    }

    public Type getSelected() {
        return selected;
    }

    public ListSetting<Type> setSelected(Type selected) {
        this.selected = selected;
        return this;
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
