package io.github.codeutilities.config.structure;

import net.minecraft.text.LiteralText;

import java.util.List;
import java.util.Optional;

public class ConfigSetting<Value> implements IRawTranslation<ConfigSetting<Value>> {

    protected final String key;
    protected Value value;
    protected Value defaultValue;

    private LiteralText rawKey = null;
    private LiteralText rawTooltip = null;
    private String keyName = null;

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
        this.rawKey = new LiteralText(key);
        return this;
    }

    @Override
    public Optional<LiteralText> getRawKey() {
        return Optional.ofNullable(rawKey);
    }

    @Override
    public ConfigSetting<Value> setRawTooltip(String key) {
        this.rawTooltip = new LiteralText(key);
        return this;
    }

    @Override
    public Optional<LiteralText> getRawTooltip() {
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
