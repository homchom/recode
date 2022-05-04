package io.github.homchom.recode.sys.player.chat.color;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public enum MinecraftColors {
    BLACK(0, 0, 0, '0', ChatFormatting.BLACK),
    DARK_BLUE(0, 0, 170, '1', ChatFormatting.DARK_BLUE),
    DARK_GREEN(0, 170, 0, '2', ChatFormatting.DARK_GREEN),
    DARK_AQUA(0, 170, 170, '3', ChatFormatting.DARK_AQUA),
    DARK_RED(170, 0, 0, '4', ChatFormatting.DARK_RED),
    DARK_PURPLE(170, 0, 170, '5', ChatFormatting.DARK_PURPLE),
    GOLD(255, 170, 0, '6', ChatFormatting.GOLD),
    GRAY(170, 170, 170, '7', ChatFormatting.GRAY),
    DARK_GRAY(85, 85, 85, '8', ChatFormatting.DARK_GRAY),
    BLUE(85, 85, 255, '9', ChatFormatting.BLUE),
    GREEN(85, 255, 85, 'a', ChatFormatting.GREEN),
    AQUA(85, 255, 255, 'b', ChatFormatting.AQUA),
    RED(255, 85, 85, 'c', ChatFormatting.RED),
    LIGHT_PURPLE(255, 85, 255, 'd', ChatFormatting.LIGHT_PURPLE),
    YELLOW(255, 255, 85, 'e', ChatFormatting.YELLOW),
    WHITE(255, 255, 255, 'f', ChatFormatting.WHITE),
    RESET(255, 255, 255, 'r', ChatFormatting.RESET);

    private static final char COLOR_CHAR = '§';
    final int r;
    final int g;
    final int b;
    final char mc;
    private final TextColor formatting;

    MinecraftColors(int r, int g, int b, char mc, ChatFormatting formatting) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.mc = mc;
        this.formatting = TextColor.fromLegacyFormat(formatting);
    }

    public static MinecraftColors fromCode(char code) {
        for (MinecraftColors colors : values()) {
            if (code == colors.getSymbol()) {
                return colors;
            }
        }
        return null;
    }

    public static String getMcFromFormatting(TextColor color) {
        for (MinecraftColors colors : values()) {
            if (color == colors.getFormatting()) {
                return colors.getMc();
            }
        }
        return null;
    }

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

    public static String hexToMc(String hex) {
        hex = hex.replaceFirst("^#", "");
        String[] chars = hex.split("");
        StringBuilder result = new StringBuilder("§x");

        for (String character : chars) {
            result.append("§").append(character.toLowerCase());
        }

        return result.toString();
    }

    public static String mcToHex(String colorCode) {
        return "#" + colorCode.replaceAll("(^§x)|§", "").toUpperCase();
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

    public TextColor getFormatting() {
        return formatting;
    }

    public String toString() {
        return getMc();
    }

}

