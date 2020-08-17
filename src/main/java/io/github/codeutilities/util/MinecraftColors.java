package io.github.codeutilities.util;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public enum MinecraftColors {
    BLACK(0, 0, 0, '0'),
    DARK_BLUE(0, 0, 170, '1'),
    DARK_GREEN(0, 170, 0, '2'),
    DARK_AQUA(0, 170, 170, '3'),
    DARK_RED(170, 0, 0, '4'),
    DARK_PURPLE(170, 0, 170, '5'),
    GOLD(255, 170, 0, '6'),
    GRAY(170, 170, 170, '7'),
    DARK_GRAY(85, 85, 85, '8'),
    BLUE(85, 85, 255, '9'),
    GREEN(85, 255, 85, 'a'),
    AQUA(85, 255, 255, 'b'),
    RED(255, 85, 85, 'c'),
    LIGHT_PURPLE(255, 85, 255, 'd'),
    YELLOW(255, 255, 85, 'e'),
    WHITE(255, 255, 255, 'f');


    int r, g, b;
    char mc;

    MinecraftColors(int r, int g, int b, char mc) {
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

    public Color getColor() {
        return new Color(r, g, b);
    }

    public String getMc() {
        return "§" + mc;
    }

    public char getSymbol() {
        return mc;
    }

    public static MinecraftColors fromCode(char code) {
        for (MinecraftColors colors : values()) {
            if (code == colors.getSymbol()) {
                return colors;
            }
        }
        return null;
    }

    public String toString() {
        return getMc();
    }

    private static final char COLOR_CHAR = '§';

    /**
     * Gets the ChatColors used at the end of the given input string.
     *
     * @param input Input string to retrieve the colors from.
     * @return Any remaining ChatColors to pass onto the next line.
     */
    @NotNull
    public static Color getLastColors(@NotNull String input) {
        Validate.notNull(input, "Cannot get last colors from null text");

        Color result = Color.WHITE;
        int length = input.length();

        // Search backwards from the end as it is faster
        for (int index = length - 1; index > -1; index--) {
            char section = input.charAt(index);
            if (section == COLOR_CHAR && index < length - 1) {
                char c = input.charAt(index + 1);
                MinecraftColors mcColor = fromCode(c);

                if (mcColor != null) {
                    return mcColor.getColor();
                }
            }
        }

        return result;
    }
}

