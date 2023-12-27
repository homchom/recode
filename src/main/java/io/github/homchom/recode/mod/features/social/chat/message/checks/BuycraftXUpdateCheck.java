package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

import java.util.regex.Pattern;

public class BuycraftXUpdateCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final Pattern BUYCRAFT_UPDATE_REGEX = Pattern.compile("^A new version of BuycraftX \\([0-9.]+\\) is available\\. Go to your server panel at https://server.tebex.io/plugins to download the update\\.$");

    @Override
    public LegacyMessageType getType() {
        return null;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return BUYCRAFT_UPDATE_REGEX.matcher(stripped).matches();
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideBuycraftUpdate();
    }
}
