package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.config.types.BooleanSetting;
import io.github.codeutilities.config.types.LongSetting;

public class ModulesGroup extends ConfigGroup {
    public ModulesGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        // TODO

        // Modules
        ConfigSubGroup time = new ConfigSubGroup("autofly");

        time.register(new BooleanSetting("autofly.enabled", false));
        this.register(time);

    }
}
