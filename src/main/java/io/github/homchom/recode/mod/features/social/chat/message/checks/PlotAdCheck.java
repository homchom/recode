package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;

public class PlotAdCheck extends MessageCheck implements StreamerModeMessageCheck {

    private static final String PLOT_AD_REGEX = "^.*\\[ Plot Ad ].*\\n.+\\n.*$";

    @Override
    public MessageType getType() {
        return MessageType.PLOT_AD;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.matches(PLOT_AD_REGEX);
    }

    @Override
    public void onReceive(Message message) {

    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hidePlotAds();
    }
}
