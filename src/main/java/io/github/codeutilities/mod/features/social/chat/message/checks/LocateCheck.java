package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.sys.networking.State;
import io.github.codeutilities.sys.player.DFInfo;

import static io.github.codeutilities.mod.events.impl.ReceiveChatMessageEvent.locating;

public class LocateCheck extends MessageCheck {

    @Override
    protected MessageType getType() {
        return MessageType.LOCATE;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.contains("\nYou are currently");
    }

    @Override
    protected void onReceive(Message message) {
        if (locating > 0) {
            DFInfo.setCurrentState(State.fromLocate(message));
            message.cancel();
            locating -= 1;
        }
    }
}
