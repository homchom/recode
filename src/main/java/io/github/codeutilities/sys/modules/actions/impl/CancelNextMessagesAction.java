package io.github.codeutilities.sys.modules.actions.impl;

import io.github.codeutilities.mod.events.register.ReceiveChatMessageEvent;
import io.github.codeutilities.sys.modules.actions.Action;
import io.github.codeutilities.sys.modules.actions.json.ActionJson;

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
