package io.github.homchom.recode.mod.features.modules.actions.impl;

import io.github.homchom.recode.Recode;
import io.github.homchom.recode.mod.features.modules.actions.Action;
import io.github.homchom.recode.mod.features.modules.actions.json.ActionJson;
import io.github.homchom.recode.sys.util.TextUtil;
import net.minecraft.network.chat.Component;

public class MessageAction extends Action {

    @Override
    public String getId() {
        return "message";
    }

    @Override
    public void execute(ActionJson params) {
        String message = params.getString("message");
        Component text = TextUtil.colorCodesToTextComponent(message);

        Recode.MC.player.displayClientMessage(text, false);
    }

}
