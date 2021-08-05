package io.github.codeutilities.mod.features.social.chat.message;

import io.github.codeutilities.mod.features.social.chat.message.checks.IncomingReportCheck;

public abstract class MessageCheck {

    private static final MessageCheck[] checks = new MessageCheck[]{
        new IncomingReportCheck()
    };

    protected abstract MessageType getType();

    protected abstract boolean check(Message message, String stripped);

    /*
        Use @code{message.cancel();} to cancel the message
    */
    protected abstract void onReceive(Message message);
//TODO provide packet in Message class to achieve the above

    public static MessageType run(Message message) {
        for (MessageCheck check : checks) {
            if (check.check(message, message.text().getString())) {
                check.onReceive(message);
                return check.getType();
            }
        }
        return MessageType.OTHER;
    }
}
