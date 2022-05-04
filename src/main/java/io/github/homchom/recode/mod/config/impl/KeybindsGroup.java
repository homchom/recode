package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.structure.*;
import io.github.homchom.recode.mod.config.types.IntegerSetting;

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
