package io.github.codeutilities.modules.triggers.impl;

import io.github.codeutilities.modules.triggers.Trigger;

public class MessageReceivedTrigger extends Trigger {

    @Override
    public String[] getEventVars() {
        return new String[]{
                "message",
                "messageWithoutColor"
        };
    }

    @Override
    public String getId() {
        return "messageReceived";
    }

}
