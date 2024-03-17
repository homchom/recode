package io.github.homchom.recode.mod.features.social.chat.message;

import io.github.homchom.recode.mod.features.social.chat.message.checks.*;

/**
 * Before a message is sent to the client, all message checks are evaluated
 */
public abstract class MessageCheck {
    // Define message checks here
    private static final MessageCheck[] checks = new MessageCheck[]{
            // General
            new DirectMessageCheck(),
            new PlotAdCheck(),
            new PlotBoostCheck(),

            // Support
            new SupportCheck(),
            new SupportQuestionCheck(),
            new SupportAnswerCheck(),

            // Moderation
            new ModerationCheck(),
            new IncomingReportCheck(),
            new SilentPunishmentCheck(),
            new ScanningCheck(),
            new TeleportCheck(),
            new JoinFailCheck(),

            // Admin
            new SpiesCheck(),
            new BuycraftXUpdateCheck(),
            new AdminCheck(),

            // Custom regex
            new StreamerModeRegexCheck()
    };

    public abstract LegacyMessageType getType();

    public abstract boolean check(LegacyMessage message, String stripped);

    /**
     * Use {@link LegacyMessage#cancel()} to cancel the message
    */
    public abstract void onReceive(LegacyMessage message);

    public static LegacyMessageType run(LegacyMessage message) {
        for (MessageCheck check : checks) {
            if (check.check(message, message.getStripped())) {
                check.onReceive(message);
                message.setCheck(check);
                return check.getType();
            }
        }
        return LegacyMessageType.OTHER;
    }
}
