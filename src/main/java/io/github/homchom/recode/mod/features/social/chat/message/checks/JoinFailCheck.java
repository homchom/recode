package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class JoinFailCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String JOIN_FAIL_REGEX = "^([^ ]{3,}) tried to join, but is banned \\(.*\\)!$";

    @Override
    public MessageType getType() {
        return MessageType.JOIN_FAIL;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.matches(JOIN_FAIL_REGEX);
    }

    @Override
    public void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
