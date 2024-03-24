package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.config.LegacyConfig;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessage;
import io.github.homchom.recode.mod.features.social.chat.message.LegacyMessageType;
import io.github.homchom.recode.mod.features.social.chat.message.MessageCheck;
import io.github.homchom.recode.mod.features.streamer.StreamerModeHandler;
import io.github.homchom.recode.mod.features.streamer.StreamerModeMessageCheck;
import io.github.homchom.recode.sys.player.chat.ChatUtil;

public class IncomingReportCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public LegacyMessageType getType() {
        return LegacyMessageType.INCOMING_REPORT;
    }

    @Override
    public boolean check(LegacyMessage message, String stripped) {
        return stripped.startsWith("! Incoming Report ");
    }

    @Override
    public void onReceive(LegacyMessage message) {
        ChatUtil.playSound(LegacyConfig.getSound("incomingReportSound"));
    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
