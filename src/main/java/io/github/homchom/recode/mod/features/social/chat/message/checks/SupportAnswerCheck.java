package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class SupportAnswerCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String SUPPORT_ANSWER_REGEX = "^.*\\n» \\w+ has answered \\w+'s question:\\n\\n.+\\n.*$";

    @Override
    public MessageType getType() {
        return MessageType.SUPPORT_ANSWER;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.matches(SUPPORT_ANSWER_REGEX);
    }

    @Override
    public void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideSupport();
    }
}
