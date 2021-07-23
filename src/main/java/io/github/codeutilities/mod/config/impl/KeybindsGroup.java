package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.IntegerSetting;

public class KeybindsGroup extends ConfigGroup {
    public KeybindsGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Keybinds
        ConfigSubGroup keybinds = new ConfigSubGroup("flightspeed");
        keybinds.register(new IntegerSetting("fsNormal", 100));
        keybinds.register(new IntegerSetting("fsMed", 300));
        keybinds.register(new IntegerSetting("fsFast", 1000));
        this.register(keybinds);

    }
}
