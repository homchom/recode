package io.github.codeutilities.config.idea.config;

import io.github.codeutilities.config.idea.structure.ConfigGroup;
import io.github.codeutilities.config.idea.structure.ConfigSubGroup;
import io.github.codeutilities.config.idea.types.IntegerSetting;

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
