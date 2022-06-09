package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.sys.networking.DFState;
import io.github.homchom.recode.sys.player.DFInfo;
import net.minecraft.network.chat.ClickEvent.Action;

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
        DFInfo.setCurrentState(DFState.fromLocate(message));
    }
}
