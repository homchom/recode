package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

public class StreamerModeRegexCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.STREAMER_MODE_REGEX;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(StreamerModeHandler.hideRegex());
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideRegexEnabled();
    }
}
