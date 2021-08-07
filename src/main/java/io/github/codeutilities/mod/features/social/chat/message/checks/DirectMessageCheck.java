package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.streamer.StreamerModeHandler;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class DirectMessageCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String DIRECT_MESSAGE_REGEX = "^\\[(\\w{3,16}) → You] .+$";

    @Override
    protected MessageType getType() {
        return MessageType.DIRECT_MESSAGE;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(DIRECT_MESSAGE_REGEX);
    }

    @Override
    protected void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideDMs();
    }
}
