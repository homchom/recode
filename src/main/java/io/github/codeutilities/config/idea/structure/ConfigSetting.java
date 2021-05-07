package io.github.codeutilities.config.idea.structure;

public class ConfigSetting<Value> {

    protected final String key;
    protected Value value;

    public ConfigSetting(String key, Value defaultValue) {
        this.key = key;
        this.value = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
