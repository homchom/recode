package io.github.codeutilities.mod.features.modules.actions.impl;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.mod.features.modules.actions.Action;
import io.github.codeutilities.mod.features.modules.actions.json.ActionJson;

public class SendMessageAction extends Action {

    @Override
    public String getId() {
        return "sendMessage";
    }

    @Override
    public void execute(ActionJson params) {
        if (CodeUtilities.MC.player != null) {
            CodeUtilities.MC.player.sendChatMessage(params.getString("message"));
        }
    }

}
