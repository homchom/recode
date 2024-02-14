package io.github.homchom.recode.sys.util;

import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    @Deprecated
    public static String toLegacyCodes(Component message) {
        StringBuilder newMsg = new StringBuilder();

        message.visit((style, string) -> {
            // color
            TextColor color = style.getColor();
            if (color == null) return Optional.empty();

            String code = MinecraftColors.getMcFromFormatting(color);
            var currentText = Objects.requireNonNullElseGet(code, () -> MinecraftColors.hexToMc(String.valueOf(color)));

            if (style.isBold()) {
                currentText += "§l";
            }
            if (style.isItalic()) {
                currentText += "§o";
            }
            if (style.isStrikethrough()) {
                currentText += "§m";
            }
            if (style.isUnderlined()) {
                currentText += "§n";
            }
            if (style.isObfuscated()) {
                currentText += "§k";
            }

            currentText += string;
            newMsg.append(currentText);
            return Optional.empty();
        }, Style.EMPTY);

        return newMsg.toString();
    }

    @Deprecated
    public static Component colorCodesToTextComponent(String message) {
        MutableComponent result = Component.literal("");

        try {
            Pattern pattern = Pattern.compile("(§[a-f0-9lonmkrA-FLONMRK]|§x(§[a-f0-9A-F]){6})");
            Matcher matcher = pattern.matcher(message);

            Style s = Style.EMPTY;

            int lastIndex = 0;
            while (matcher.find()) {
                int start = matcher.start();
                String text = message.substring(lastIndex, start);
                if (!text.isEmpty()) {
                    MutableComponent t = Component.literal(text);
                    t.setStyle(s);
                    result.append(t);
                }
                String col = matcher.group();

                if (col.length() == 2) {
                    s = s.applyLegacyFormat(ChatFormatting.getByCode(col.charAt(1)));
                } else {
                    var color = TextColor.parseColor("#" + col.replaceAll("§", "")
                            .substring(1));
                    s = Style.EMPTY.withColor(color.getOrThrow(false, (string) -> {}));
                }
                lastIndex = matcher.end();
            }
            String text = message.substring(lastIndex);
            if (!text.isEmpty()) {
                MutableComponent t = Component.literal(text);
                t.setStyle(s);
                result.append(t);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return Component.literal("Text Error");
        }

        return result;
    }

    public static String toString(Component text) {
        if (text.getString().isEmpty()) {
            return "{\"text\": \"\"}";
        }
        return "{\"extra\":[" + String.join(",", toExtraString(text.getSiblings()))
            + "],\"text\":\"\"}";
    }

    private static ArrayList<String> toExtraString(List<Component> siblings) {
        ArrayList<String> result = new ArrayList<>();
        for (Component sibling : siblings) {
            result.add("{\"text\": \"" + sibling.getString() +
                "\", \"color\": \"" + sibling.getStyle().getColor() +
                "\", \"bold\": " + sibling.getStyle().isBold() +
                ", \"italic\": " + sibling.getStyle().isItalic() +
                ", \"underlined\": " + sibling.getStyle().isUnderlined() +
                ", \"strikethrough\": " + sibling.getStyle().isStrikethrough() +
                ", \"obfuscated\": " + sibling.getStyle().isObfuscated() + "}");
        }
        return result;
    }

    public static String toTextString(String text) {
        return TextUtil.toString(TextUtil.colorCodesToTextComponent(text.replaceAll("&", "§").replaceAll("\"", "''").replaceAll("''", "\\\\\"")));
    }

    public static String toUncoloredString(String text){
        return TextUtil.toString(TextUtil.colorCodesToTextComponent(text.replaceAll("\"", "''").replaceAll("''", "\\\\\"")));
    }

    // TODO: document every invariant of this function (because it needs to be rewritten)
    public static String formatValues(String text, String lastColor, String stringColor, String numberColor) {
        StringBuilder output = new StringBuilder();
        Character lastChar = null;
        boolean activeQuote = false;

        for (char character : text.toCharArray()) {
            if (character == '"') {
                activeQuote = !activeQuote;
                if (!activeQuote) {
                    output.append(character).append(lastColor);
                    lastChar = character;
                    continue;
                }
            }

            if (Objects.equals(lastColor, "§")) {
                lastColor = "§" + character;
            } else if (!activeQuote) {
                if (Character.isDigit(character)) {
                    if (lastChar != null && (lastChar == '.' || lastChar == ',')) {
                        output.deleteCharAt(output.length() - 1);
                        output.append(numberColor).append(lastChar);
                    }
                    // color any number, marked by being outside of quotation marks
                    output.append(numberColor).append(character).append(lastColor);
                    lastChar = character;
                    continue;
                }
            }
            if (character == '§') { lastColor = "§"; }
            if (activeQuote) {
                output.append(stringColor); // Color any string, marked by 2 quotation marks
            }
            output.append(character);
            lastChar = character;
        }
        return output.toString();
    }

    public static String formatValues(String text) {
        return TextUtil.formatValues(text, "§7", "§b", "§c");
    }
}
