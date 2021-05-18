package io.github.codeutilities.modules.actions.impl;

import io.github.codeutilities.events.register.ReceiveChatMessageEvent;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;

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
