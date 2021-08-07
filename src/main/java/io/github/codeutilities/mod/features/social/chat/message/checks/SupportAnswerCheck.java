package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.streamer.StreamerModeHandler;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class SupportAnswerCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String SUPPORT_ANSWER_REGEX = "^.*\\n» \\w+ has answered \\w+'s question:\\n\\n.+\\n.*$";

    @Override
    protected MessageType getType() {
        return MessageType.SUPPORT_ANSWER;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(SUPPORT_ANSWER_REGEX);
    }

    @Override
    protected void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideSupport();
    }
}
