package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.sys.player.DFInfo;
import net.minecraft.text.ClickEvent.Action;

public class LocateCheck extends MessageCheck {

    @Override
    public MessageType getType() {
        return MessageType.LOCATE;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.contains("\nYou are currently") &&
            message.getText().getStyle().getClickEvent().getAction() == Action.RUN_COMMAND;
    }

    @Override
    public void onReceive(Message message) {
        DFInfo.setCurrentState(State.fromLocate(message));
    }
}
