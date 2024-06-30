package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

import java.util.regex.Pattern;

public class ScanningCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final Pattern SCANNING_REGEX = Pattern.compile("^Scanning \\w+(.|\n)*\\[Online] \\[Offline] \\[(IP|)Banned]\1*$");

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.SCANNING;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return SCANNING_REGEX.matcher(stripped).matches();
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
