package io.github.codeutilities.mod.config.types.list;

import io.github.codeutilities.mod.config.structure.ConfigSetting;

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
        return this instanceof StringListSetting;
    }
}
