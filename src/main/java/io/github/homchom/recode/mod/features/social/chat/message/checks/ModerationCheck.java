package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class ModerationCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public MessageType getType() {
        return MessageType.MODERATION;
    }

    @Override
    public boolean check(Message message, String stripped) {
        // General moderation messages (Broadcast, AntiX, etc.)
        return stripped.startsWith("[MOD]");
    }

    @Override
    public void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
