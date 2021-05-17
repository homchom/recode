package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;

public class ModulesGroup extends ConfigGroup {
    public ModulesGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        // TODO

        // Modules
        ConfigSubGroup time = new ConfigSubGroup("autofly")
                .setRawKey("fjhdsfhsdjf")
                .setRawTooltip("fsdfsdf");

        //time.register(new BooleanSetting("autofly.enabled", false));
        this.register(time);

    }
}
