package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;

import java.util.regex.Pattern;

public class PlotAdCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final Pattern PLOT_AD_REGEX = Pattern.compile(" {32}\\[ Plot Ad ] {32}\\n(.+)\\n {78}");

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.PLOT_AD;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return PLOT_AD_REGEX.matcher(stripped).matches();
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hidePlotAds();
    }
}
