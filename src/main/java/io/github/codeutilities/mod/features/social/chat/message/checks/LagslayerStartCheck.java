package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.CPU_UsageText;
import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;

public class LagslayerStartCheck extends MessageCheck {

    private static final String LAGSLAYER_START_REGEX = "^\\[LagSlayer] Now monitoring plot .*\\. Type /lagslayer to stop monitoring\\.$";

    @Override
    protected MessageType getType() {
        return MessageType.LAGSLAYER_START;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.matches(LAGSLAYER_START_REGEX);
    }

    @Override
    protected void onReceive(Message message) {
        CPU_UsageText.lagSlayerEnabled = true;
    }
}
