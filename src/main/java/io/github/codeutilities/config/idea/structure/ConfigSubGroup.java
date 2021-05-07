package io.github.codeutilities.config.idea.structure;

import io.github.codeutilities.commands.sys.IManager;

import java.util.ArrayList;
import java.util.List;

public class ConfigSubGroup implements IManager<ConfigSetting<?>> {
    private final List<ConfigSetting<?>> settings = new ArrayList<>();
    private final String name;

    public ConfigSubGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Deprecated
    @Override
    public void initialize() {/*not needed*/}

    @Override
    public void register(ConfigSetting<?> object) {
        this.settings.add(object);
    }

    @Override
    public List<ConfigSetting<?>> getRegistered() {
        return settings;
    }
}
