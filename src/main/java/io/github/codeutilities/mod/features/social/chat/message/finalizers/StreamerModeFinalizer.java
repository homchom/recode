package io.github.codeutilities.mod.features.social.chat.message.finalizers;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageFinalizer;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;
import io.github.codeutilities.mod.features.social.chat.message.checks.DirectMessageCheck;
import io.github.codeutilities.mod.features.streamer.StreamerModeMessageCheck;

public class StreamerModeFinalizer extends MessageFinalizer {

    private static final String[] HIDE_DMS_EXEMPTIONS = new String[]{
            "RyanLand",
            "Vattendroppen236",
            "Reasonless"
    };

    @Override
    protected void receive(Message message) {
        MessageCheck check = message.getCheck();

        if (
                check instanceof StreamerModeMessageCheck
                && ((StreamerModeMessageCheck) check).streamerHideEnabled()
                && !matchesDirectMessageExemptions(message)
        ) {
            message.cancel();
        }
    }

    private static boolean matchesDirectMessageExemptions(Message message) {
        if (message.typeIs(MessageType.DIRECT_MESSAGE)) {
            String stripped = message.getStripped();

            for (String username : HIDE_DMS_EXEMPTIONS) {
                if (DirectMessageCheck.usernameMatches(message, username)) {
                    return true;
                }
            }
        }
        return false;
    }
}
