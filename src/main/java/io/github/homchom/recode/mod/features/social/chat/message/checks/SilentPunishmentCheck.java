package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class SilentPunishmentCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public MessageType getType() {
        return MessageType.SILENT_PUNISHMENT;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.startsWith("[Silent]");
    }

    @Override
    public void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
