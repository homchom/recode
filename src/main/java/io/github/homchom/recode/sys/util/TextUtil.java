package io.github.homchom.recode.sys.util;

import io.github.homchom.recode.sys.player.chat.color.MinecraftColors;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;

import java.util.*;
import java.util.regex.*;

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
        MutableComponent result = new TextComponent("");

        try {
            Pattern pattern = Pattern.compile("(§[a-f0-9lonmkrA-FLONMRK]|§x(§[a-f0-9A-F]){6})");
            Matcher matcher = pattern.matcher(message);

            Style s = Style.EMPTY;

            int lastIndex = 0;
            while (matcher.find()) {
                int start = matcher.start();
                String text = message.substring(lastIndex, start);
                if (text.length() != 0) {
                    MutableComponent t = new TextComponent(text);
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
                MutableComponent t = new TextComponent(text);
                t.setStyle(s);
                result.append(t);
            }
        } catch (Exception err) {
            err.printStackTrace();
            return new TextComponent("Recode Text Error");
        }

        return result;
    }

//    public static Text colorCodesToTextComponent(String message) {
//        if(message.equals("")) return Text.Serializer.fromJson("{\"text\": \"\"}");
//        message = "§f" + message;
//        String sibling, literalColorCodes, color = null, text;
//        String bold, italic, underlined, strikethrough, obfuscated;
//        List<String> sections = new java.util.ArrayList<>(Collections.emptyList());
//        List<String> literalColorSections = new java.util.ArrayList<>(Collections.emptyList());
//        MutableText result = Text.Serializer.fromJson("{\"text\": \"\"}");
//        char literalColorCode;
//        int lastColorOccurrence;
//
//        Pattern pattern = Pattern.compile("(§x(§[^§]){6}([^§]|§[lomnk])+)|(§([^§]|§[lomnk])+)");
//        Matcher matcher = pattern.matcher(message);
//        while (matcher.find()) sections.add(matcher.group());
//
//        for (String section : sections) {
//            sibling = section;
//
//            // reset modifiers
//            bold = "null";
//            italic = "null";
//            underlined = "null";
//            strikethrough = "null";
//            obfuscated = "null";
//
//            // text
//            text = sibling.replaceAll("^(§.)+", "");
//
//            // color
//            pattern = Pattern.compile("(§x(§([0-f]|r)){6})|(§([0-f]|r))");
//            matcher = pattern.matcher(sibling);
//            while (matcher.find()) literalColorSections.add(matcher.group());
//            literalColorCodes = literalColorSections.get(literalColorSections.size() - 1);
//            if (literalColorSections.size() - 1 != 0) {
//                literalColorCode = literalColorCodes.charAt(1);
//                if (literalColorCode == 'x') color = MinecraftColors.mcToHex(literalColorCodes);
//                else
//                    color = String.valueOf(Objects.requireNonNull(MinecraftColors.fromCode(literalColorCodes.charAt(1))).getFormatting());
//            }
//
//            // modifiers
//            lastColorOccurrence = sibling.lastIndexOf(literalColorCodes);
//            if (sibling.indexOf("§l") > lastColorOccurrence) bold = "true";
//            if (sibling.indexOf("§o") > lastColorOccurrence) italic = "true";
//            if (sibling.indexOf("§n") > lastColorOccurrence) underlined = "true";
//            if (sibling.indexOf("§m") > lastColorOccurrence) strikethrough = "true";
//            if (sibling.indexOf("§k") > lastColorOccurrence) obfuscated = "true";
//
//            // serializer
//            assert false;
//            if (!text.equals("")) {
//                result.append(Text.Serializer.fromJson("{" +
//                        "\"text\": \"" + text +
//                        "\", \"color\": \"" + color +
//                        "\", \"bold\": \"" + bold +
//                        "\", \"italic\": \"" + italic +
//                        "\", \"underlined\": \"" + underlined +
//                        "\", \"strikethrough\": \"" + strikethrough +
//                        "\", \"obfuscated\": \"" + obfuscated +
//                        "\"}"));
//            }
//        }
//
//        return result;
//    }

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
        return TextUtil.toString(
                TextUtil.colorCodesToTextComponent(text.replaceAll("&", "§").replaceAll("\"", "''")))
            .replaceAll("''", "\\\\\"");
    }

}
