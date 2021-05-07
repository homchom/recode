package io.github.codeutilities.config.idea.config;

import io.github.codeutilities.config.idea.structure.ConfigGroup;
import io.github.codeutilities.config.idea.structure.ConfigSubGroup;
import io.github.codeutilities.config.idea.types.BooleanSetting;
import io.github.codeutilities.config.idea.types.StringSetting;

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
