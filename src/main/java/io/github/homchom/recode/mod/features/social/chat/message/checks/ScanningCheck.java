package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class ScanningCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String SCANNING_REGEX = "^Scanning \\w+(.|\n)*\\[Online] \\[Offline] \\[(IP|)Banned]\1*$";

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.SCANNING;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(SCANNING_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
