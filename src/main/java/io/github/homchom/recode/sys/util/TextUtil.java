package io.github.homchom.recode.sys.util;

import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static String textComponentToColorCodes(Component message) {
        List<Component> siblings = message.getSiblings();

        StringBuilder newMsg = new StringBuilder();
        String currentText = "";

        translateSibling(message, newMsg, currentText);
        for (Component sibling : siblings) {
            translateSibling(sibling, newMsg, currentText);
            for (Component sibling2 : sibling.getSiblings()) {
                translateSibling(sibling2, newMsg, currentText);
                for (Component sibling3 : sibling2.getSiblings()) {
                    translateSibling(sibling3, newMsg, currentText);
                }
            }
        }

        return newMsg.toString();
    }

    private static void translateSibling(Component sibling, StringBuilder newMsg, String currentText) {
        Style style = sibling.getStyle();

        // color
        TextColor color = style.getColor();
        if (color == null && sibling.getSiblings().size() > 0) {
            return;
        }

        String code = MinecraftColors.getMcFromFormatting(color);
        if (code == null) {
            currentText = MinecraftColors.hexToMc(String.valueOf(color));
        } else {
            currentText = code;
        }

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

        currentText += sibling.getString();
        newMsg.append(currentText);
    }

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
                if (text.length() != 0) {
                    MutableComponent t = Component.literal(text);
                    t.setStyle(s);
                    result.append(t);
                }
                String col = matcher.group();

                if (col.length() == 2) {
                    s = s.applyLegacyFormat(ChatFormatting.getByCode(col.charAt(1)));
                } else {
                    s = Style.EMPTY.withColor(
                        TextColor.parseColor("#" + col.replaceAll("§", "").substring(1)));
                }
                lastIndex = matcher.end();
            }
            String text = message.substring(lastIndex);
            if (text.length() != 0) {
                MutableComponent t = Component.literal(text);
                t.setStyle(s);
                result.append(t);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return Component.literal("Recode Text Error");
        }

        return result;
    }

    public static String toString(Component text) {
        if (text.getString().equals("")) {
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

    public static String formatValues(String text, String lastColor,String stringColor, String numberColor) {
        String output = "";
        String lastChar = "";
        Boolean activeQuote = false;
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            String character = String.valueOf(ch);
            if (character.equals("\"")) {
                activeQuote = !activeQuote;
                if (!activeQuote) {
                    output += character;
                    output += lastColor;
                    lastChar = character;
                    continue;
                }
            }
            if (Objects.equals(lastColor, "§")) {
                lastColor = "§" + character;
            }else if (!activeQuote) {
                if (character.matches("\\d")) {
                    if (lastChar.equals(".")) {
                        output = output.substring(0, output.length() - 1) + numberColor + ".";
                    }
                    output = output + numberColor + character + lastColor; // Color any number, marked by being outside of quotation marks
                    lastChar = character;
                    continue;
                }
            }
            if (character.matches("§")) { lastColor = "§"; }
            if (activeQuote) {
                output += stringColor; // Color any string, marked by 2 quotation marks
            }
            output += character;
            lastChar = character;
        }
        return output;
    }

    public static String formatValues(String text) {
        return TextUtil.formatValues(text, "§7", "§b", "§c");
    }
}
