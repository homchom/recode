package io.github.homchom.recode.mod.features.modules.actions.impl;

import io.github.homchom.recode.mod.features.modules.actions.Action;
import io.github.homchom.recode.mod.features.modules.actions.json.ActionJson;
import io.github.homchom.recode.sys.player.chat.MessageGrabber;

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
