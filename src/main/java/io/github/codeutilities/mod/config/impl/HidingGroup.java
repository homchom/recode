package io.github.codeutilities.mod.config.impl;

import io.github.codeutilities.mod.config.structure.ConfigGroup;
import io.github.codeutilities.mod.config.structure.ConfigSubGroup;
import io.github.codeutilities.mod.config.types.BooleanSetting;
import io.github.codeutilities.mod.config.types.StringSetting;

public class HidingGroup extends ConfigGroup {
    public HidingGroup(String name) {
        super(name);
    }

    @Override
    public void initialize() {
        // Non sub-grouped
        this.register(new BooleanSetting("hideJoinLeaveMessages", false));
        this.register(new BooleanSetting("hideVarScopeMessages", false));
        this.register(new BooleanSetting("stackDuplicateMsgs", false));

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
