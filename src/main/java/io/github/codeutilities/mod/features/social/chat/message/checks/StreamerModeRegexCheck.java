package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.streamer.StreamerModeHandler;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class StreamerModeRegexCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    protected MessageType getType() {
        return MessageType.STREAMER_MODE_REGEX;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(StreamerModeHandler.hideRegex());
    }

    @Override
    protected void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideRegexEnabled();
    }
}
