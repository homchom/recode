package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class PlotAdCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String PLOT_AD_REGEX = " {32}\\[ Plot Ad ] {32}\\n(.+)\\n {78}";

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.PLOT_AD;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(PLOT_AD_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hidePlotAds();
    }
}
