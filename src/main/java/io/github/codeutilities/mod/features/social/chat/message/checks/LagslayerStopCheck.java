package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.CPU_UsageText;
import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;

public class LagslayerStopCheck extends MessageCheck {

    private static final String LAGSLAYER_STOP_REGEX =
            "(^\\[LagSlayer] Stopped monitoring plot .*\\.$)|" +
            "(^Error: You must be in a plot to use this command!$)|" +
            "(^Error: You can't monitor this plot!$)";

    @Override
    protected MessageType getType() {
        return MessageType.LAGSLAYER_STOP;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(LAGSLAYER_STOP_REGEX);
    }

    @Override
    protected void onReceive(Message message) {
        CPU_UsageText.lagSlayerEnabled = false;
    }
}
