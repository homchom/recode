package io.github.codeutilities.mod.features.social.chat.message.checks;

import io.github.codeutilities.mod.features.social.chat.message.Message;
import io.github.codeutilities.mod.features.social.chat.message.MessageCheck;
import io.github.codeutilities.mod.features.social.chat.message.MessageType;

public class IncomingReportCheck extends MessageCheck {

    @Override
    protected MessageType getType() {
        return MessageType.INCOMING_REPORT;
    }

    @Override
    protected boolean check(Message message, String stripped) {
        return stripped.startsWith("! Incoming Report ");
    }

    @Override
    protected void onReceive(Message message) {
    
    }
}
