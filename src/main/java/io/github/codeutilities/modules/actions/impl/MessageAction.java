package io.github.codeutilities.modules.actions.impl;

import io.github.codeutilities.CodeUtilities;
import io.github.codeutilities.modules.actions.Action;
import io.github.codeutilities.modules.actions.json.ActionJson;
import io.github.codeutilities.util.chat.TextUtil;
import net.minecraft.text.Text;

public class MessageAction extends Action {

    @Override
    public String getId() {
        return "message";
    }

    @Override
    public void execute(ActionJson params) {
        String message = params.getString("message");
        Text text = TextUtil.colorCodesToTextComponent(message);

        CodeUtilities.MC.player.sendMessage(text, false);
    }

}
