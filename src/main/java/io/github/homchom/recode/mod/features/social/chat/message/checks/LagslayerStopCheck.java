package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.LagslayerHUD;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.social.chat.message.MessageType;

public class LagslayerStopCheck extends MessageCheck {

    private static final String LAGSLAYER_STOP_REGEX =
            "(^\\[LagSlayer] Stopped monitoring plot .*\\.$)|" +
            "(^Error: You must be in a plot to use this command!$)|" +
            "(^Error: You can't monitor this plot!$)";

    @Override
    public MessageType getType() {
        return MessageType.LAGSLAYER_STOP;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(LAGSLAYER_STOP_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {
        LagslayerHUD.lagSlayerEnabled = false;
    }
}
