package io.github.homchom.recode.mod.config.structure;

import io.github.homchom.recode.mod.config.types.DropdownSetting;
import net.minecraft.network.chat.*;

import java.util.*;

public class ConfigSetting<Value> implements IRawTranslation<ConfigSetting<Value>> {

    protected final String key;
    protected Value value;
    protected Value defaultValue;

    private Component rawKey = null;
    private Component rawTooltip = null;
    private String keyName = null;
    private String description = null;

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

    public void setDescription(String description) {
        this.description = description;
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public ConfigSetting<Value> setKeyName(String key) {
        this.keyName = key;
        return this;
    }

    @Override
    public Optional<String> getKeyName() {
        return Optional.ofNullable(keyName);
    }

    @Override
    public ConfigSetting<Value> setRawKey(String key) {
        this.rawKey = Component.literal(key);
        return this;
    }

    @Override
    public Optional<Component> getRawKey() {
        return Optional.ofNullable(rawKey);
    }

    @Override
    public ConfigSetting<Value> setRawTooltip(String key) {
        this.rawTooltip = Component.literal(key);
        return this;
    }

    @Override
    public Optional<Component> getRawTooltip() {
        return Optional.ofNullable(rawTooltip);
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

    public boolean isEnum() {
        return value instanceof Enum;
    }

    public boolean isDropdown() {
        return this instanceof DropdownSetting;
    }

    public boolean isText() {
        return value instanceof Component;
    }

    public String getCustomKey() {
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
