package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class AdminCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.ADMIN;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.startsWith("[ADMIN]");
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideAdmin();
    }
}
