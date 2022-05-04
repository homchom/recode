package io.github.homchom.recode.mod.config.types;

import io.github.homchom.recode.mod.config.types.list.StringListSetting;

import java.util.*;

public class DropdownSetting<E extends Enum<E> & IConfigDropdownEnum<E>> extends StringListSetting {

    public DropdownSetting(String key, String... defaultValue) {
        super(key, defaultValue);
    }

    public static <T extends Enum<T> & IConfigDropdownEnum<T>> String[] fromEnum(T anEnum) {
        List<String> result = new ArrayList<>();
        for (T value : anEnum.getValues()) {
            result.add(value.getName());
        }
        return result.toArray(new String[0]);
    }
}
