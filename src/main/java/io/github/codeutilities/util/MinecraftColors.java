package io.github.codeutilities.util;

public enum MinecraftColors {
    BLACK(0, 0, 0, "§0"),
    DARK_BLUE(0, 0, 170, "§1"),
    DARK_GREEN(0, 170, 0, "§2"),
    DARK_AQUA(0, 170, 170, "§3"),
    DARK_RED(170, 0, 0, "§4"),
    DARK_PURPLE(170, 0, 170, "§5"),
    GOLD(255, 170, 0, "§6"),
    GRAY(170, 170, 170, "§7"),
    DARK_GRAY(85, 85, 85, "§8"),
    BLUE(85, 85, 255, "§9"),
    GREEN(85, 255, 85, "§a"),
    AQUA(85, 255, 255, "§b"),
    RED(255, 85, 85, "§c"),
    LIGHT_PURPLE(255, 85, 255, "§d"),
    YELLOW(255, 255, 85, "§e"),
    WHITE(255, 255, 255, "§f");


    int r, g, b;
    String mc;

    MinecraftColors(int r, int g, int b, String mc) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.mc = mc;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public String getMc() {
        return mc;
    }

    public String toString() {
        return mc;
    }
}

