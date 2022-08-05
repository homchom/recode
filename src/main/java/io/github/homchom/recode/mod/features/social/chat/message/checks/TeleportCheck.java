package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class TeleportCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String TELEPORTING_REGEX = "^\\[([^ ]{3,}): Teleported ([^ ]{3,}) to ([^ ]{3,})]$";

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.TELEPORTING;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(TELEPORTING_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
