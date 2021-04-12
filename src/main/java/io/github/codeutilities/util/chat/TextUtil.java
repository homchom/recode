package io.github.codeutilities.util.chat;

import io.github.codeutilities.util.color.MinecraftColors;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    public static String textComponentToColorCodes(Text message) {
        List<Text> siblings = message.getSiblings();

        StringBuilder newMsg = new StringBuilder();
        String currentText;

        for (Text sibling : siblings) {
            Style style = sibling.getStyle();

            // color
            TextColor color = style.getColor();
            String literalColor = String.valueOf(color);
            String code = MinecraftColors.getMcFromFormatting(color);
            if (code == null) currentText = MinecraftColors.hexToMc(literalColor);
            else currentText = code;

            if (style.isBold()) currentText = currentText + "§l";
            if (style.isItalic()) currentText = currentText + "§o";
            if (style.isStrikethrough()) currentText = currentText + "§m";
            if (style.isUnderlined()) currentText = currentText + "§n";
            if (style.isObfuscated()) currentText = currentText + "§k";

            currentText = currentText + sibling.getString();
            newMsg.append(currentText);
        }

        return newMsg.toString();
    }

    public static Text colorCodesToTextComponent(String message) {
        message = "§f" + message;
        Matcher siblings = null;
        String sibling, literalColorCodes = null, color, prevcolor = "reset", text;
        String bold = "null", italic = "null", underlined = "null", strikethrough = "null", obfuscated = "null";
        MutableText result = Text.Serializer.fromJson("{\"text\": \"\"}");
        char literalColorCode;
        int lastColorOccurrence;

        System.out.println("INPUT = " + message);

        Pattern pattern = Pattern.compile("/(§x(§[0-frlomnk]){6}[^§]*)|(§[0-frlomnk][^§]*)/g");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            System.out.println(matcher);
            System.out.println(matcher.group(0));
            siblings = matcher;
        }

        System.out.println("SIBLINGS = " + siblings);

        for (int i = 0; i < siblings.groupCount(); i++) {
            sibling = siblings.group(i);

            // text
            text = sibling.replaceAll("^(§.)+", "");

            // color
            pattern = Pattern.compile("(§x(§([0-f]|r)){6})|(§([0-f]|r))");
            matcher = pattern.matcher(sibling);
            while (matcher.find()) literalColorCodes = matcher.group(matcher.groupCount() - 1);
            assert literalColorCodes != null;
            if (matcher.groupCount() == 0) {
                color = prevcolor;
            } else {
                literalColorCode = literalColorCodes.charAt(1);
                if (literalColorCode == 'x') color = MinecraftColors.mcToHex(literalColorCodes);
                else
                    color = String.valueOf(Objects.requireNonNull(MinecraftColors.fromCode(literalColorCodes.charAt(1))).getFormatting());

                bold = "null";
                italic = "null";
                underlined = "null";
                strikethrough = "null";
                obfuscated = "null";
            }
            prevcolor = color;

            // modifiers
            lastColorOccurrence = sibling.lastIndexOf(literalColorCodes);
            if (sibling.indexOf("§b") > lastColorOccurrence) bold = "true";
            if (sibling.indexOf("§o") > lastColorOccurrence) italic = "true";
            if (sibling.indexOf("§n") > lastColorOccurrence) underlined = "true";
            if (sibling.indexOf("§m") > lastColorOccurrence) strikethrough = "true";
            if (sibling.indexOf("§k") > lastColorOccurrence) obfuscated = "true";

            // serializer
            assert false;
            result.append(Text.Serializer.fromJson("{" +
                    "\"text\": \"" + text +
                    "\", \"color\": \"" + color +
                    "\", \"bold\": \"" + bold +
                    "\", \"italic\": \"" + italic +
                    "\", \"underlined\": \"" + underlined +
                    "\", \"strikethrough\": \"" + strikethrough +
                    "\", \"obfuscated\": \"" + obfuscated +
                    "\"}"));
        }

        System.out.println(result);
        return result;
    }

}
