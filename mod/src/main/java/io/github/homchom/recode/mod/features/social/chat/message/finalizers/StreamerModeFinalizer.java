package io.github.homchom.recode.mod.features.social.chat.message.finalizers;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.social.chat.message.MessageFinalizer;
import io.github.homchom.recode.mod.features.social.chat.message.checks.DirectMessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

public class StreamerModeFinalizer extends MessageFinalizer {

    private static final String[] HIDE_DMS_EXEMPTIONS = new String[]{
            "RyanLand",
            "Vattendroppen236",
            "Reasonless"
    };

    @Override
    protected void receive(LegacyMessage message) {
        MessageCheck check = message.getCheck();

        if (
                check instanceof StreamerModeMessageCheck
                && ((StreamerModeMessageCheck) check).streamerHideEnabled()
                && !matchesDirectMessageExemptions(message)
        ) {
            message.cancel();
        }
    }

    private static boolean matchesDirectMessageExemptions(LegacyMessage message) {
        if (message.typeIs(LegacyMessageType.DIRECT_MESSAGE)) {
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
