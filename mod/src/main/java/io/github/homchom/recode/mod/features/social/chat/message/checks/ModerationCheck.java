package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

public class ModerationCheck extends MessageCheck implements StreamerModeMessageCheck {
    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.MODERATION;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        // General moderation messages (Broadcast, AntiX, etc.)
        return stripped.startsWith("[MOD]");
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
