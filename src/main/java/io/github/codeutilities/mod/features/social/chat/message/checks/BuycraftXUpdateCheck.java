package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.streamer.StreamerModeHandler;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class BuycraftXUpdateCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String BUYCRAFT_UPDATE_REGEX = "^A new version of BuycraftX \\([0-9.]+\\) is available\\. Go to your server panel at https://server.tebex.io/plugins to download the update\\.$";

    @Override
    protected MessageType getType() {
        return null;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(BUYCRAFT_UPDATE_REGEX);
    }

    @Override
    protected void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideBuycraftUpdate();
    }
}
