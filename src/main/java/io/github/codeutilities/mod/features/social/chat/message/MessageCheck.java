package io.github.codeutilities.mod.features.social.chat.message;

import io.github.codeutilities.mod.features.social.chat.message.checks.IncomingReportCheck;

public abstract class MessageCheck {

    private static final MessageCheck[] checks = new MessageCheck[]{
        new IncomingReportCheck()
    };

    public abstract MessageType getType();

    public abstract boolean check(Message message, String stripped);

    public static MessageType run(Message message) {
        for (MessageCheck check : checks) {
            if (check.check(message, message.text().getString())) {
                return check.getType();
            }
        }
        return MessageType.OTHER;
    }
}
