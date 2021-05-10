package io.github.codeutilities.config.config;

import io.github.codeutilities.config.structure.ConfigGroup;
import io.github.codeutilities.config.structure.ConfigSubGroup;
import io.github.codeutilities.config.types.BooleanSetting;
import io.github.codeutilities.config.types.LongSetting;

public class AutomationGroup extends ConfigGroup {
    public AutomationGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Automation
        ConfigSubGroup autoMessage = new ConfigSubGroup("automsg");
        autoMessage.register(new BooleanSetting("automsg", true));
        autoMessage.register(new BooleanSetting("automsg_timeout", true));
        autoMessage.register(new LongSetting("automsg_timeoutNumber", 300000L));
        this.register(autoMessage);

        // Time
        ConfigSubGroup time = new ConfigSubGroup("time");
        time.register(new BooleanSetting("autotime", false));
        time.register(new LongSetting("autotimeval", 0L));
        this.register(time);

        // Other non sub-grouped
        this.register(new BooleanSetting("autoRC", false));
        this.register(new BooleanSetting("autonightvis", false));
        this.register(new BooleanSetting("autofly", false));
        this.register(new BooleanSetting("autolagslayer", false));
        this.register(new BooleanSetting("autoChatLocal", false));
    }
}
