package io.github.codeutilities.mod.features.modules.actions.impl;

import io.github.codeutilities.mod.features.modules.actions.Action;
import io.github.codeutilities.mod.features.modules.actions.json.ActionJson;
import io.github.codeutilities.sys.player.chat.MessageGrabber;

public class CancelNextMessagesAction extends Action {

    @Override
    public String getId() {
        return "cancelNextMessages";
    }

    @Override
    public void execute(ActionJson params) {
        MessageGrabber.hide(params.getInt("amount"));
    }

}
