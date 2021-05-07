package io.github.codeutilities.config.idea.structure;

import io.github.codeutilities.commands.sys.IManager;
import io.github.codeutilities.config.idea.config.*;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager implements IManager<ConfigGroup> {
    private final List<ConfigGroup> groups = new ArrayList<>();
    private static ConfigManager instance;

    public ConfigManager() {
        instance = this;
    }

    @Override
    public void initialize() {
        this.register(new AutomationGroup("automation"));
        this.register(new CommandsGroup("commands"));
        this.register(new HidingGroup("hiding"));
        this.register(new KeybindsGroup("keybinds"));
        this.register(new HidingGroup("highlight"));
        this.register(new ScreenGroup("screen"));
        this.register(new MiscellaneousGroup("misc"));
    }

    @Override
    public void register(ConfigGroup object) {
        this.groups.add(object);
    }

    @Override
    public List<ConfigGroup> getRegistered() {
        return groups;
    }

    public ConfigSetting<?> find(String key) {
        return groups.stream()
                .flatMap(group -> group.getRegistered().stream())
                .flatMap(configSubGroup -> configSubGroup.getRegistered().stream())
                .filter(configSetting -> configSetting.getKey().equalsIgnoreCase(key))
                .findFirst().orElse(null);
    }

    public static ConfigManager getInstance() {
        return instance;
    }
}
