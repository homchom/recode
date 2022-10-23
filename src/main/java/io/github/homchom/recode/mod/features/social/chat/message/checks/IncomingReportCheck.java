package io.github.homchom.recode.mod.features.social.chat.message.checks;

import io.github.homchom.recode.mod.config.Config;
import io.github.homchom.recode.mod.features.social.chat.message.*;
import io.github.homchom.recode.mod.features.streamer.*;
import io.github.homchom.recode.sys.player.chat.ChatUtil;

public class IncomingReportCheck extends MessageCheck implements StreamerModeMessageCheck {

    @Override
    public MessageType getType() {
        return MessageType.INCOMING_REPORT;
    }

    @Override
    public boolean check(Message message, String stripped) {
        return stripped.startsWith("! Incoming Report ");
    }

    @Override
    public void onReceive(Message message) {
        ChatUtil.playSound(Config.getSound("incomingReportSound"));
    }

    @Override
    public boolean streamerHideEnabled() {
        return StreamerModeHandler.hideModeration();
    }
}
