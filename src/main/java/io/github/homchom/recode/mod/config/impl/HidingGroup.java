package io.github.homchom.recode.mod.config.impl;

import io.github.homchom.recode.mod.config.structure.*;
import io.github.homchom.recode.mod.config.types.*;

public class HidingGroup extends ConfigGroup {
    public HidingGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("hideJoinLeaveMessages", false));
        this.register(new BooleanSetting("hideVarScopeMessages", false));
        this.register(new BooleanSetting("hideScoreboardOnF3", true));

        // Regular Expressions
        ConfigSubGroup regex = new ConfigSubGroup("regex");
        regex.register(new BooleanSetting("hideMsgMatchingRegex", false));
        regex.register(new StringSetting("hideMsgRegex", ""));
        this.register(regex);

        // Staff
        ConfigSubGroup staff = new ConfigSubGroup("staff");
        staff.register(new BooleanSetting("hideSessionSpy", false));
        staff.register(new BooleanSetting("hideMutedChat", false));
        this.register(staff);

    }
}
