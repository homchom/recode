package io.github.homchom.recode.mod.features.modules.actions.impl;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.features.modules.actions.Action;
import io.github.homchom.recode.mod.features.modules.actions.json.ActionJson;

public class SendMessageAction extends Action {

    @Override
    public String getId() {
        return "sendMessage";
    }

    @Override
    public void execute(ActionJson params) {
        if (Recode.MC.player != null) {
            Recode.MC.player.chat(params.getString("message"));
        }
    }

}
