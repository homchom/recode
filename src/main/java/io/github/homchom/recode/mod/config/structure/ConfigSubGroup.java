package io.github.homchom.recode.mod.config.structure;

import io.github.homchom.recode.mod.commands.IManager;
import net.minecraft.network.chat.TextComponent;

import java.util.*;

public class ConfigSubGroup implements IManager<ConfigSetting<?>>, IRawTranslation<ConfigSubGroup> {
    private final List<ConfigSetting<?>> settings = new ArrayList<>();
    private boolean startExpanded = true;
    private final String name;

    private TextComponent rawKey = null;
    private TextComponent rawTooltip = null;

    public ConfigSubGroup(String name) {
        this.name = name;
    }

    @Override
    public ConfigSubGroup setRawKey(String key) {
        this.rawKey = new TextComponent(key);
        return this;
    }

    @Override
    public Optional<TextComponent> getRawKey() {
        return Optional.ofNullable(rawKey);
    }

    @Override
    public ConfigSubGroup setRawTooltip(String key) {
        this.rawTooltip = new TextComponent(key);
        return this;
    }

    @Override
    public Optional<TextComponent> getRawTooltip() {
        return Optional.ofNullable(rawTooltip);
    }

    public String getName() {
        return name;
    }

    public ConfigSubGroup setStartExpanded(boolean startExpanded) {
        this.startExpanded = startExpanded;
        return this;
    }

    public boolean isStartExpanded() {
        return startExpanded;
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
