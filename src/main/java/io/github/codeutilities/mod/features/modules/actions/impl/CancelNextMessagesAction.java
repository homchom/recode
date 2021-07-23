package io.github.codeutilities.mod.features.modules.actions.impl;

import io.github.codeutilities.mod.events.impl.ReceiveChatMessageEvent;
import io.github.codeutilities.mod.features.modules.actions.Action;
import io.github.codeutilities.mod.features.modules.actions.json.ActionJson;

public class CancelNextMessagesAction extends Action {

    @Override
    public String getId() {
        return "cancelNextMessages";
    }

    @Override
    public void execute(ActionJson params) {
        ReceiveChatMessageEvent.cancelMsgs = params.getInt("amount");
    }

}
