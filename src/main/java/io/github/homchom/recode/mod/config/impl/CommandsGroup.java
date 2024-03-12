package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.structure.ConfigGroup;
import io.github.homchom.recode.mod.config.structure.ConfigSubGroup;
import io.github.homchom.recode.mod.config.types.BooleanSetting;
import io.github.homchom.recode.mod.config.types.IntegerSetting;
import io.github.homchom.recode.mod.config.types.LongSetting;
import io.github.homchom.recode.mod.config.types.StringSetting;

public class CommandsGroup extends ConfigGroup {
    public CommandsGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // top-level
        this.register(new BooleanSetting("dfCommands", true));
        this.register(new BooleanSetting("errorSound", true));
        this.register(new BooleanSetting("colorReplacePicker", false));

        // auto /msg
        ConfigSubGroup autoMessage = new ConfigSubGroup("automsg");
        autoMessage.register(new BooleanSetting("automsg", false));
        autoMessage.register(new BooleanSetting("automsg_timeout", true));
        autoMessage.register(new LongSetting("automsg_timeoutNumber", 300000L));
        this.register(autoMessage);

        // heads
        ConfigSubGroup heads = new ConfigSubGroup("heads");
        heads.register(new BooleanSetting("headsEnabled", false));
        heads.register(new IntegerSetting("headMenuMaxRender", 1000));
        this.register(heads);

        // copyval
        ConfigSubGroup copyVal = new ConfigSubGroup("copyval");
        copyVal.register(new StringSetting("itemTextFormatLocation", "[%f, %f, %f, %f, %f]"));
        this.register(copyVal);
    }
}
