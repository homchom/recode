package io.github.codeutilities.util.chat;

import io.github.codeutilities.util.color.MinecraftColors;
import net.minecraft.text.*;

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
        Pattern pattern = Pattern.compile("(&x(&([0-f]|r)){6}[^&]+)|(&([0-f]|r)[^&]+)");
        Matcher matcher = pattern.matcher(message);
        Matcher siblings = null;
        String sibling, color, text;
        MutableText result = null;
        while (matcher.find()) siblings = matcher;

        for (int i = 0; i < matcher.groupCount(); i++) {
            assert siblings != null;
            sibling = siblings.group(i);

            text = sibling.replaceAll("^(&.)+","");
            color = String.valueOf(Objects.requireNonNull(MinecraftColors.fromCode(sibling.charAt(1))).getFormatting());

            assert false;
            result.append(Text.Serializer.fromJson("{" +
                    "\"text\": \"" + text +
                    "\", \"color\": \"" + color +
                    /*
                    "\", \"bold\": \"" + bold +
                    "\", \"italic\": \"" + italic +
                    "\", \"underlined\": \"" + underlined +
                    "\", \"strikethrough\": \"" + strikethrough +
                    "\", \"obfuscated\": \"" + obfuscated +
                     */
                    "\"}"));


        }

        System.out.println(result);
        return result;
    }

}
