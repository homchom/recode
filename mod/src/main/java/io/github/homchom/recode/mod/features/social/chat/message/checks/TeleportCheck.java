package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

import java.util.regex.Pattern;

public class TeleportCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final Pattern TELEPORTING_REGEX = Pattern.compile("^\\[([^ ]{3,}): Teleported ([^ ]{3,}) to ([^ ]{3,})]$");

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.TELEPORTING;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return TELEPORTING_REGEX.matcher(stripped).matches();
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
