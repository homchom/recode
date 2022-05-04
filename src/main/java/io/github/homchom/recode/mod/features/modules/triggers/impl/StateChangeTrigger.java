package io.github.homchom.recode.mod.features.modules.triggers.impl;

import io.github.homchom.recode.mod.features.modules.triggers.Trigger;

public class StateChangeTrigger extends Trigger {

    @Override
    public String getId() {
        return "stateChange";
    }

    @Override
    public String[] getEventVars() {
        return new String[]{
                "from",
                "to"
        };
    }

}
