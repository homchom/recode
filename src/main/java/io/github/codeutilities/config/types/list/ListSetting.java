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

    @Override
    public boolean isString() {
        return selected.getClass().isAssignableFrom(String.class);
    }

    @Override
    public boolean isInteger() {
        return selected.getClass().isAssignableFrom(Integer.class);
    }

    @Override
    public boolean isDouble() {
        return selected.getClass().isAssignableFrom(Double.class);
    }

    @Override
    public boolean isFloat() {
        return selected.getClass().isAssignableFrom(Float.class);
    }

    @Override
    public boolean isLong() {
        return selected.getClass().isAssignableFrom(Long.class);
    }

    @Override
    public boolean isList() {
        return selected.getClass().isAssignableFrom(List.class);
    }

    @Override
    public boolean isBoolean() {
        return selected.getClass().isAssignableFrom(Boolean.class);
    }
}
