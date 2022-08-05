package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class JoinFailCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String JOIN_FAIL_REGEX = "^([^ ]{3,}) tried to join, but is banned \\(.*\\)!$";

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.JOIN_FAIL;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(JOIN_FAIL_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
