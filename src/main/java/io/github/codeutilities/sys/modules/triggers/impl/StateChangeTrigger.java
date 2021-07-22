package io.github.codeutilities.sys.modules.triggers.impl;

import io.github.codeutilities.sys.modules.triggers.Trigger;

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
