package io.github.homchom.recode.mod.config.structure;

import io.github.homchom.recode.mod.commands.IManager;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class ConfigGroup implements IManager<ConfigSubGroup>, IRawTranslation<ConfigGroup> {
    private final List<ConfigSetting<?>> settings = new ArrayList<>();
    private final List<ConfigSubGroup> subGroups = new ArrayList<>();
    private final String name;

    private Component rawKey = null;

    public ConfigGroup(String name) {
        this.name = name;
    }

    @Override
    public ConfigGroup setRawKey(String key) {
        this.rawKey = Component.literal(key);
        return this;
    }

    @Override
    public Optional<Component> getRawKey() {
        return Optional.ofNullable(rawKey);
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
}
