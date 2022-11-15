package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class BuycraftXUpdateCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String BUYCRAFT_UPDATE_REGEX = "^A new version of BuycraftX \\([0-9.]+\\) is available\\. Go to your server panel at https://server.tebex.io/plugins to download the update\\.$";

    @Override
    public MessageType getType() {
        return null;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(BUYCRAFT_UPDATE_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideBuycraftUpdate();
    }
}
