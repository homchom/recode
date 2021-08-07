package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.streamer.StreamerModeHandler;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class SupportCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    protected MessageType getType() {
        return MessageType.SUPPORT;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        // General support messages (Broadcast, session requests and completion, etc.)
        return stripped.startsWith("[SUPPORT]");
    }

    @Override
    protected void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideSupport();
    }
}
