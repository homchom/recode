package io.github.homchom.recode.mod.features.social.chat.message;

public enum LegacyMessageType {
    OTHER,

    // ----------------------------------

    // General
    DIRECT_MESSAGE(true),
    PLOT_AD(true),
    PLOT_BOOST(true),

    // Support
    SUPPORT,
    SUPPORT_QUESTION(true),
    SUPPORT_ANSWER(true),

    // Moderation
    MODERATION,
    INCOMING_REPORT,
    SILENT_PUNISHMENT,
    SCANNING(2),
    TELEPORTING,
    JOIN_FAIL,

    // Admin
    SPIES,
    ADMIN,

    // Custom regex
    STREAMER_MODE_REGEX;

    // ----------------------------------

    private final int messageAmount;
    private final boolean hasSound;

    LegacyMessageType() {
        this(1);
    }

    LegacyMessageType(int messageAmount) {
        this(messageAmount, false);
    }

    LegacyMessageType(boolean hasSound) {
        this(1, hasSound);
    }

    LegacyMessageType(int messageAmount, boolean hasSound) {
        this.messageAmount = messageAmount;
        this.hasSound = hasSound;
    }

    public int getMessageAmount() {
        return messageAmount;
    }

    public boolean hasSound() {
        return hasSound;
    }

    public boolean is(LegacyMessageType compareTo) {
        return this == compareTo;
    }
}
