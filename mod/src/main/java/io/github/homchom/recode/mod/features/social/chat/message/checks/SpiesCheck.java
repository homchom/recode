package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

public class SpiesCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.SPIES;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        // Hide spies (Session spy, Muted spy, DM spy)
        return stripped.startsWith("*");
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideSpies();
    }
}
