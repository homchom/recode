package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.mod.config.types.IntegerSetting;
import io.github.codeutilities.mod.config.types.LongSetting;

public class CommandsGroup extends ConfigGroup {
    public CommandsGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("dfCommands", true));
        this.register(new BooleanSetting("errorSound", true));

        // Auto /msg
        ConfigSubGroup autoMessage = new ConfigSubGroup("automsg");
        autoMessage.register(new BooleanSetting("automsg", true));
        autoMessage.register(new BooleanSetting("automsg_timeout", true));
        autoMessage.register(new LongSetting("automsg_timeoutNumber", 300000L));
        this.register(autoMessage);

        // Heads
        ConfigSubGroup heads = new ConfigSubGroup("heads");
        heads.register(new BooleanSetting("headsEnabled", false));
        heads.register(new IntegerSetting("headMenuMaxRender", 1000));
        this.register(heads);

        // Colors
        ConfigSubGroup colors = new ConfigSubGroup("colors");
        colors.register(new IntegerSetting("colorMaxRender", 158));
        colors.register(new IntegerSetting("colorLines", 5));
        colors.register(new BooleanSetting("colorReplacePicker", false));
        this.register(colors);

        // Non sub-grouped
        this.register(new BooleanSetting("cmdLoadPlots", true));
    }
}
