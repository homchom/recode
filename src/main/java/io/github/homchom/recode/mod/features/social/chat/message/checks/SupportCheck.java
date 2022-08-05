package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class SupportCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.SUPPORT;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        // General support messages (Broadcast, session requests and completion, etc.)
        return stripped.startsWith("[SUPPORT]");
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideSupport();
    }
}
