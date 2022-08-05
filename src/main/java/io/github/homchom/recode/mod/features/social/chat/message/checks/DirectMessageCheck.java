package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.ConversationTimer;
import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class DirectMessageCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String DIRECT_MESSAGE_REGEX = "^\\[(\\w{3,16}) → You] .+$";

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.DIRECT_MESSAGE;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(DIRECT_MESSAGE_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {
        // update conversation end timer
        if (ConversationTimer.currentConversation != null && usernameMatches(message, ConversationTimer.currentConversation)) {
            ConversationTimer.conversationUpdateTime = String.valueOf(System.currentTimeMillis());
        }
    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideDMs();
    }

    public static boolean usernameMatches(LegacyMessage message, String username) {
        return message.getStripped().matches("^\\["+ username +" → You] .+$");
    }
}
