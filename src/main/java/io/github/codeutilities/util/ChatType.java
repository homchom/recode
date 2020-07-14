package io.github.codeutilities.util;

public enum ChatType {
    SUCCESS("§2§l➤ §a"),
    FAIL("§4§l➤ §c"),
    INFO_YELLOW("§6§l➤ §e"),
    INFO_BLUE("§9§l➤ §b");

    private final String prefix;

    private ChatType(final String prefix) {
        this.prefix = prefix;
    }

    public String getString() {
        return this.prefix;
    }
}
