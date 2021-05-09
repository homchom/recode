package io.github.codeutilities.config.structure;

import io.github.codeutilities.commands.sys.IManager;

import java.util.ArrayList;
import java.util.List;

public abstract class ConfigGroup implements IManager<ConfigSubGroup> {
    private final List<ConfigSetting<?>> settings = new ArrayList<>();
    private final List<ConfigSubGroup> subGroups = new ArrayList<>();
    private final String name;

    public ConfigGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void register(ConfigSetting<?> setting) {
        settings.add(setting);
    }

    @Override
    public void register(ConfigSubGroup object) {
        this.subGroups.add(object);
    }

    @Override
    public List<ConfigSubGroup> getRegistered() {
        return subGroups;
    }

    public List<ConfigSetting<?>> getSettings() {
        return settings;
    }

    public ConfigSetting<?> findSetting(String key) {
        return settings.stream()
                .filter(setting -> setting.getKey().equalsIgnoreCase(key))
                .findFirst().orElse(null);
    }
}
