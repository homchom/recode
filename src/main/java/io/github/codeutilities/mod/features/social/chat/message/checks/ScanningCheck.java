package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.streamer.StreamerModeHandler;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class ScanningCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String SCANNING_REGEX = "^Scanning \\w+(.|\n)*\\[Online] \\[Offline] \\[(IP|)Banned]\1*$";

    @Override
    protected MessageType getType() {
        return MessageType.SCANNING;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(SCANNING_REGEX);
    }

    @Override
    protected void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
