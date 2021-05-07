package io.github.codeutilities.util.misc;

import io.github.codeutilities.CodeUtilities;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.lang.reflect.Field;
import java.util.Arrays;

public class StringUtil {

    public static LiteralText of(String... literalTexts) {
        int length = literalTexts.length;
        LiteralText text = new LiteralText(literalTexts[0]);

        if (length == 1) {
            return text;
        }

        for (String currentText : Arrays.copyOfRange(literalTexts, 1, length)) {
            text.append(currentText);
        }
        return text;
    }

    public static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) {
            throw new IllegalArgumentException();
        }
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
        return builder.toString();
    }

    public static String genDummyIntArray() {
        return "[I;" + CodeUtilities.RANDOM.nextInt() + "," + CodeUtilities.RANDOM.nextInt() + ","
                + CodeUtilities.RANDOM.nextInt() + "," + CodeUtilities.RANDOM.nextInt() + "]";
    }

    public static String textToString(Text txt) {
        StringBuilder out = new StringBuilder();

        Style s = txt.getStyle();

        TextColor color = s.getColor();

        if (color != null) {
            try {
                Formatting f = Formatting.valueOf(color.getName().toUpperCase());
                Class<Formatting> cl = Formatting.class;
                Field code = cl.getDeclaredField("code");
                code.setAccessible(true);
                out.append("§").append(code.get(f));
            } catch (Exception err) {
                StringBuilder code = new StringBuilder();
                for (char c : Integer.toHexString(color.getRgb()).toUpperCase().toCharArray()) {
                    code.append("§").append(c);
                }
                while (code.length() < 12) {
                    code.insert(0, "§0");
                }

                out.append("§x").append(code);
            }
        }

        if (s.isBold()) {
            out.append("§l");
        }
        if (s.isItalic()) {
            out.append("§o");
        }
        if (s.isStrikethrough()) {
            out.append("§n");
        }
        if (s.isUnderlined()) {
            out.append("§m");
        }
        if (s.isObfuscated()) {
            out.append("§k");
        }

        out.append(txt.asString());

        for (Text sibling : txt.getSiblings()) {
            out.append(textToString(sibling));
        }

        return out.toString();
    }
}
