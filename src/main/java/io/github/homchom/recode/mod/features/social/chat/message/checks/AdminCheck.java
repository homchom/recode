package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class AdminCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public MessageType getType() {
        return MessageType.ADMIN;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.startsWith("[ADMIN]");
    }

    @Override
    public void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideAdmin();
    }
}
