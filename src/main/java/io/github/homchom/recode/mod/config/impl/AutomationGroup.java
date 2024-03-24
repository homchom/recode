package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.BooleanSetting;
import io.github.homchom.recode.mod.config.types.LongSetting;

public class AutomationGroup extends ConfigGroup {
    public AutomationGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {

        // Time
        ConfigSubGroup time = new ConfigSubGroup("time");
        time.register(new BooleanSetting("autotime", false));
        time.register(new LongSetting("autotimeval", 0L));
        this.register(time);

        // Other non sub-grouped
        this.register(new BooleanSetting("autowand", false));
        this.register(new BooleanSetting("autoRC", false));
        this.register(new BooleanSetting("autonightvis", false));
        this.register(new BooleanSetting("autolagslayer", false));
        this.register(new BooleanSetting("autoChatLocal", false));
        this.register(new BooleanSetting("autoClickEditMsgs", true));
        this.register(new BooleanSetting("autoTip", true));
    }
}
