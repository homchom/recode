package io.github.codeutilities.mod.features.social.chat.message;

public enum MessageType {

    UNKNOWN,
    OTHER,

    INCOMING_REPORT;

    private final boolean hasSound;

    MessageType() {
        this(false);
    }

    MessageType(boolean hasSound) {
        this.hasSound = hasSound;
    }

    public boolean hasSound() {
        return hasSound;
    }
}
