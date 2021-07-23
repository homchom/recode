package io.github.codeutilities.mod.config.structure;

import io.github.codeutilities.mod.commands.IManager;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigSubGroup implements IManager<ConfigSetting<?>>, IRawTranslation<ConfigSubGroup> {
    private final List<ConfigSetting<?>> settings = new ArrayList<>();
    private boolean startExpanded = true;
    private final String name;

    private LiteralText rawKey = null;
    private LiteralText rawTooltip = null;

    public ConfigSubGroup(String name) {
        this.name = name;
    }

    @Override
    public ConfigSubGroup setRawKey(String key) {
        this.rawKey = new LiteralText(key);
        return this;
    }

    @Override
    public Optional<LiteralText> getRawKey() {
        return Optional.ofNullable(rawKey);
    }

    @Override
    public ConfigSubGroup setRawTooltip(String key) {
        this.rawTooltip = new LiteralText(key);
        return this;
    }

    @Override
    public Optional<LiteralText> getRawTooltip() {
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
