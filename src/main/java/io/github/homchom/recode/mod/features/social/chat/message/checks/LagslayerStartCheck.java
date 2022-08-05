package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.features.LagslayerHUD;
import io.github.homchom.recode.mod.features.social.chat.message.*;

public class LagslayerStartCheck extends MessageCheck {

    private static final String LAGSLAYER_START_REGEX = "^\\[LagSlayer] Now monitoring plot .*\\. Type /lagslayer to stop monitoring\\.$";

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.LAGSLAYER_START;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.matches(LAGSLAYER_START_REGEX);
    }

    @Override
    public void onReceive(LegacyMessage message) {
        LagslayerHUD.lagSlayerEnabled = true;
    }
}
