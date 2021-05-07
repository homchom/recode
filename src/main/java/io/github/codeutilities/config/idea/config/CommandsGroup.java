package io.github.codeutilities.config.idea.config;

import io.github.codeutilities.config.idea.structure.ConfigGroup;
import io.github.codeutilities.config.idea.structure.ConfigSubGroup;
import io.github.codeutilities.config.idea.types.BooleanSetting;
import io.github.codeutilities.config.idea.types.IntegerSetting;

public class CommandsGroup extends ConfigGroup {
    public CommandsGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("dfCommands", true));
        this.register(new BooleanSetting("errorSound", true));

        // Heads
        ConfigSubGroup heads = new ConfigSubGroup("heads");
        heads.register(new BooleanSetting("headsEnabled", false));
        heads.register(new IntegerSetting("headMenuMaxRender", 1000));
        this.register(heads);

        // Colors
        ConfigSubGroup colors = new ConfigSubGroup("colors");
        colors.register(new IntegerSetting("colorMaxRender", 158));
        colors.register(new IntegerSetting("colorLines", 5));
        this.register(colors);

        // Non sub-grouped
        this.register(new BooleanSetting("cmdLoadPlots", true));
    }
}
